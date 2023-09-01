package com.server.domain.video.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.entity.CategoryRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.video.service.dto.response.VideoCreateUrlResponse;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.domain.watch.entity.Watch;
import com.server.domain.watch.repository.WatchRepository;
import com.server.global.exception.businessexception.categoryexception.CategoryNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoAccessDeniedException;
import com.server.global.exception.businessexception.videoexception.VideoFileNameNotMatchException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoUploadNotRequestException;
import com.server.module.redis.service.RedisService;
import com.server.module.s3.service.AwsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ChannelRepository channelRepository;
    private final WatchRepository watchRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final AwsService awsService;
    private final RedisService redisService;

    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository,
                        ChannelRepository channelRepository, WatchRepository watchRepository, CategoryRepository categoryRepository,
                        CartRepository cartRepository, AwsService awsService, RedisService redisService) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
        this.channelRepository = channelRepository;
        this.watchRepository = watchRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.awsService = awsService;
        this.redisService = redisService;
    }

    public Page<VideoPageResponse> getVideos(Long loginMemberId, int page, int size, String sort, String category, boolean subscribe) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Video> video = videoRepository.findAllByCategoryPaging(category, pageRequest, sort, loginMemberId, subscribe);

        List<Boolean> isPurchaseInOrder = isPurchaseInOrder(loginMemberId, video.getContent());

        List<Boolean> isSubscribeInOrder = isSubscribeInOrder(loginMemberId, video.getContent(), subscribe);

        List<String[]> urlsInOrder = getThumbnailAndImageUrlsInOrder(video.getContent());

        return VideoPageResponse.of(video, isPurchaseInOrder, isSubscribeInOrder, urlsInOrder);
    }

    @Transactional
    public VideoDetailResponse getVideo(Long loginMemberId, Long videoId) {

        Video video = existVideo(videoId);

        Member member = verifiedMemberOrNull(loginMemberId);

        watch(member, video);

        Boolean subscribed = isSubscribed(member, video);

        Map<String, Boolean> isPurchaseAndIsReplied = getIsPurchaseAndIsReplied(member, videoId);

        Map<String, String> urls = getVideoUrls(video);

        return VideoDetailResponse.of(video, subscribed, urls, isPurchaseAndIsReplied);
    }

    @Transactional
    public VideoCreateUrlResponse getVideoCreateUrl(Long loginMemberId, VideoCreateUrlServiceRequest request) {

        verifiedMember(loginMemberId);

        redisService.setExpire(String.valueOf(loginMemberId), request.getFileName(), 60 * 15); // 15분

        return VideoCreateUrlResponse.builder()
                .videoUrl(awsService.getUploadVideoUrl(loginMemberId, request.getFileName()))
                .thumbnailUrl(awsService.getUploadThumbnailUrl(loginMemberId, request.getFileName(), request.getImageType()))
                .build();
    }

    @Transactional
    public Long createVideo(Long loginMemberId, VideoCreateServiceRequest request) {

        Member member = verifiedMemberWithChannel(loginMemberId);

        checkFileName(loginMemberId, request.getVideoName());

        List<Category> categories = verifiedCategories(request.getCategories());

        Video video = Video.createVideo(
                member.getChannel(),
                request.getVideoName(),
                request.getPrice(),
                request.getDescription(),
                categories);

        return videoRepository.save(video).getVideoId();
    }

    @Transactional
    public void updateVideo(Long loginMemberId, VideoUpdateServiceRequest request) {

        Video video = verifedVideo(loginMemberId, request.getVideoId());

        List<Category> categories = verifiedCategories(request.getCategories());

        video.updateVideo(request.getVideoName(), request.getPrice(), request.getDescription());

        video.updateCategory(categories);
    }

    @Transactional
    public Boolean changeCart(Long loginMemberId, Long videoId) {

        Video video = existVideo(videoId);

        Member member = verifiedMember(loginMemberId);

        return createOrDeleteCart(member, video);
    }

    @Transactional
    public void deleteVideo(Long loginMemberId, Long videoId) {

        Video video = verifedVideo(loginMemberId, videoId);

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

        urls.put("videoUrl", awsService.getVideoUrl(owner.getMemberId(), video.getVideoFile()));
        urls.put("thumbnailUrl", getThumbnailUrl(owner.getMemberId(), video));
        urls.put("imageUrl", getImageUrl(owner));

        return urls;
    }

    private String getImageUrl(Member member) {
        return awsService.getImageUrl(member.getImageFile());
    }

    private String getThumbnailUrl(Long memberId, Video video) {
        return awsService.getThumbnailUrl(memberId, video.getThumbnailFile());
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

    private void checkFileName(Long memberId, String requestFileName) {

        String savedFileName = redisService.getData(String.valueOf(memberId));

        if(savedFileName == null) {
            throw new VideoUploadNotRequestException();
        }

        if(!savedFileName.equals(requestFileName)) {
            throw new VideoFileNameNotMatchException();
        }

        redisService.deleteData(String.valueOf(memberId));
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
        cartRepository.save(Cart.createCart(member, video, video.getPrice()));
        return true;
    }
}