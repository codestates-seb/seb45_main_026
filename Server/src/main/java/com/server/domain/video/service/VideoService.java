package com.server.domain.video.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.dto.ReplyCreateServiceApi;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.video.service.dto.response.VideoCreateUrlResponse;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.domain.watch.entity.Watch;
import com.server.domain.watch.repository.WatchRepository;
import com.server.global.exception.businessexception.categoryexception.CategoryNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotValidException;
import com.server.global.exception.businessexception.videoexception.VideoAccessDeniedException;
import com.server.global.exception.businessexception.videoexception.VideoClosedException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
    private final WatchRepository watchRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final AwsService awsService;
    private final ReplyRepository replyRepository;

    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository,
                        WatchRepository watchRepository, CategoryRepository categoryRepository,
                        CartRepository cartRepository, AwsService awsService, ReplyRepository replyRepository) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
        this.watchRepository = watchRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.awsService = awsService;
        this.replyRepository = replyRepository;
    }

    public Page<VideoPageResponse> getVideos(Long loginMemberId, VideoGetServiceRequest request) {

        Page<Video> video = videoRepository.findAllByCategoryPaging(request.toDataRequest());

        List<Boolean> isPurchaseInOrder = isPurchaseInOrder(loginMemberId, video.getContent());

        List<Boolean> isSubscribeInOrder = isSubscribeInOrder(loginMemberId, video.getContent(), request.isSubscribe());

        List<String[]> urlsInOrder = getThumbnailAndImageUrlsInOrder(video.getContent());

        return VideoPageResponse.of(video, isPurchaseInOrder, isSubscribeInOrder, urlsInOrder);
    }

    @Transactional
    public VideoDetailResponse getVideo(Long loginMemberId, Long videoId) {

        Video video = existVideo(videoId);

        Member member = verifiedMemberOrNull(loginMemberId);

        Map<String, Boolean> isPurchaseAndIsReplied = getIsPurchaseAndIsReplied(member, videoId);

        checkIfVideoClosed(isPurchaseAndIsReplied.get("isPurchased"), video);

        Boolean subscribed = isSubscribed(member, video);

        Map<String, String> urls = getVideoUrls(video);

        watch(member, video);

        return VideoDetailResponse.of(video, subscribed, urls, isPurchaseAndIsReplied);
    }

    @Transactional
    public VideoCreateUrlResponse getVideoCreateUrl(Long loginMemberId, VideoCreateUrlServiceRequest request) {

        Member member = verifiedMemberWithChannel(loginMemberId);

        Video video = Video.createVideo(
                member.getChannel(),
                request.getFileName(),
                0,
                "uploading",
                new ArrayList<>());

        videoRepository.save(video);

        String location = video.getVideoId() + "/" + request.getFileName();

        return VideoCreateUrlResponse.builder()
                .videoUrl(getUploadVideoUrl(loginMemberId, location))
                .thumbnailUrl(getUploadThumbnailUrl(loginMemberId, location, request.getImageType()))
                .build();
    }

    @Transactional
    public Long createVideo(Long loginMemberId, VideoCreateServiceRequest request) {

        Video video = verifedVideo(loginMemberId, request.getVideoName());

        additionalCreateProcess(request, video);

        return video.getVideoId();
    }

    private void additionalCreateProcess(VideoCreateServiceRequest request, Video video) {

        List<Category> categories = verifiedCategories(request.getCategories());

        video.additionalCreateProcess(request.getPrice(), request.getDescription());

        video.updateCategory(categories);
    }

    @Transactional
    public void updateVideo(Long loginMemberId, VideoUpdateServiceRequest request) {

        Video video = verifedVideo(loginMemberId, request.getVideoId());

        video.updateVideo(request.getDescription());
    }

    @Transactional
    public Boolean changeCart(Long loginMemberId, Long videoId) {

        Video video = existVideo(videoId);

        Member member = verifiedMember(loginMemberId);

        return createOrDeleteCart(member, video);
    }

    @Transactional
    public void deleteCarts(Long loginMemberId, List<Long> videoIds) {

        Member member = verifiedMember(loginMemberId);

        cartRepository.deleteByMemberAndVideoIds(member, videoIds);
    }

    @Transactional
    public void deleteVideo(Long loginMemberId, Long videoId) {

        Video video = verifedVideo(loginMemberId, videoId);

        awsService.deleteFile(loginMemberId, video.getVideoName(), FileType.VIDEO);
        awsService.deleteFile(loginMemberId, video.getThumbnailFile(), FileType.THUMBNAIL);

        videoRepository.delete(video);
    }

    private List<Boolean> isPurchaseInOrder(Long loginMemberId, List<Video> content) {

        List<Long> videoIds = content.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberPurchaseVideos(loginMemberId, videoIds);
    }

    private List<Boolean> isSubscribeInOrder(Long loginMemberId, List<Video> content, boolean subscribe) {

        if (subscribe) {
            return IntStream.range(0, content.size())
                    .mapToObj(i -> true)
                    .collect(Collectors.toList());
        }

        List<Long> memberIds = content.stream()
                .map(video -> video.getChannel().getMember().getMemberId())
                .collect(Collectors.toList());

        return memberRepository.checkMemberSubscribeChannel(loginMemberId, memberIds);
    }

    private List<String[]> getThumbnailAndImageUrlsInOrder(List<Video> content) {

        return content.stream()
                .map(video -> {
                    Member member = video.getChannel().getMember();
                    String[] urls = new String[2];
                    urls[0] = getThumbnailUrl(member.getMemberId(), video);
                    urls[1] = getImageUrl(member);
                    return urls;
                })
                .collect(Collectors.toList());
    }

    private Map<String, String> getVideoUrls(Video video) {

        Map<String, String> urls = new HashMap<>();

        Member owner = video.getChannel().getMember();

        urls.put("videoUrl", awsService.getFileUrl(owner.getMemberId(), video.getVideoFile(), FileType.VIDEO));
        urls.put("thumbnailUrl", getThumbnailUrl(owner.getMemberId(), video));
        urls.put("imageUrl", getImageUrl(owner));

        return urls;
    }

    private String getImageUrl(Member member) {
        return awsService.getFileUrl(member.getMemberId(), member.getImageFile(), FileType.PROFILE_IMAGE);
    }

    private String getThumbnailUrl(Long memberId, Video video) {
        return awsService.getFileUrl(memberId, video.getThumbnailFile(), FileType.THUMBNAIL);
    }


    private String getUploadVideoUrl(Long loginMemberId, String location) {
        return awsService.getUploadVideoUrl(loginMemberId, location);
    }

    private String getUploadThumbnailUrl(Long loginMemberId, String location, ImageType imageType) {
        return awsService.getImageUploadUrl(loginMemberId,
                location,
                FileType.THUMBNAIL,
                imageType);
    }

    private Member verifiedMemberOrNull(Long loginMemberId) {
        return memberRepository.findById(loginMemberId)
                .orElse(null);
    }

    private Member verifiedMember(Long loginMemberId) {
        return memberRepository.findById(loginMemberId).orElseThrow(MemberNotFoundException::new);
    }

    private Member verifiedMemberWithChannel(Long loginMemberId) {
        return memberRepository.findByIdWithChannel(loginMemberId).orElseThrow(MemberNotFoundException::new);
    }

    private Video existVideo(Long videoId) {
        return videoRepository.findVideoDetail(videoId)
                .orElseThrow(VideoNotFoundException::new);
    }

    private Video verifedVideo(Long memberId, Long videoId) {
        Video video = videoRepository.findVideoDetail(videoId)
                .orElseThrow(VideoNotFoundException::new);

        if(!video.getChannel().getMember().getMemberId().equals(memberId)) {
            throw new VideoAccessDeniedException();
        }

        return video;
    }

    private Video verifedVideo(Long memberId, String videoName) {

        return videoRepository.findVideoByNameWithMember(memberId, videoName)
                .orElseThrow(VideoNotFoundException::new);
    }

    private void watch(Member member, Video video) {

        video.addView();
        if(member == null) return;

        getOrCreateWatch(member, video);
    }

    private void getOrCreateWatch(Member member, Video video) {
        Watch watch = watchRepository.findByMemberAndVideo(member, video)
                .orElseGet(() -> watchRepository.save(Watch.createWatch(member, video)));

        watch.setLastWatchedTime(LocalDateTime.now());
    }

    private void checkIfVideoClosed(boolean isPurchased, Video video) {
        if(!isPurchased && video.getVideoStatus().equals(VideoStatus.CLOSED)) {
            throw new VideoClosedException(video.getVideoName());
        }
    }

    private Map<String, Boolean> getIsPurchaseAndIsReplied(Member member, Long videoId) {

        if(member == null) {
            return Map.of("isPurchased", false, "isReplied", false);
        }

        Long loginMemberId = member.getMemberId();

        List<Boolean> purchasedAndIsReplied = videoRepository.isPurchasedAndIsReplied(loginMemberId, videoId);

        HashMap<String, Boolean> isPurchaseAndIsReplied = new HashMap<>();
        isPurchaseAndIsReplied.put("isPurchased", purchasedAndIsReplied.get(0));
        isPurchaseAndIsReplied.put("isReplied", purchasedAndIsReplied.get(1));

        return isPurchaseAndIsReplied;
    }

    private Boolean isSubscribed(Member member, Video video) {

        if(member == null) return false;

        return memberRepository.checkMemberSubscribeChannel(
                member.getMemberId(),
                List.of(video.getChannel().getMember().getMemberId())).get(0);
    }

    private List<Category> verifiedCategories(List<String> categoryNames) {
        List<Category> categories = categoryRepository.findByCategoryNameIn(categoryNames);

        if(categories.size() != categoryNames.size()) {
            throw new CategoryNotFoundException();
        }

        return categories;
    }

    private Boolean createOrDeleteCart(Member member, Video video) {

        Cart cart = cartRepository.findByMemberAndVideo(member, video).orElse(null);

        return cart == null ? createCart(member, video) : deleteCart(cart);
    }

    private boolean deleteCart(Cart cart) {
        cartRepository.delete(cart);
        return false;
    }

    private boolean createCart(Member member, Video video) {

        if(video.getVideoStatus().equals(VideoStatus.CLOSED)) {
            throw new VideoClosedException(video.getVideoName());
        }

        cartRepository.save(Cart.createCart(member, video, video.getPrice()));
        return true;
    }

    public Page<ReplyInfo> getReplies(Long videoId, int page, int size, String sort) {

        PageRequest pageRequest = PageRequest.of(page, size);

        return ReplyInfo.of(replyRepository.findAllByReplyId(pageRequest, sort, videoId));
    }

    public Long createReply(Long loginMemberId, Long videoId, ReplyCreateServiceApi response) {

        memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberAccessDeniedException());

        Integer star = response.getStar();

        if (star < 1 || star > 5) {
            throw new ReplyNotValidException();
        }

        videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException());

        Reply reply = Reply.builder()
                .content(response.getContent())
                .star(response.getStar())
                .build();

        return replyRepository.save(reply).getReplyId();
    }
}
