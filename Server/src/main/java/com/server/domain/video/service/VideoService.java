package com.server.domain.video.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
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
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.videoexception.*;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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

    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository,
                        WatchRepository watchRepository, CategoryRepository categoryRepository,
                        CartRepository cartRepository, AwsService awsService) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
        this.watchRepository = watchRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.awsService = awsService;
    }

    public Page<VideoPageResponse> getVideos(Long loginMemberId, VideoGetServiceRequest request) {

        Member member = verifiedMemberOrNull(loginMemberId);

        Page<Video> videos = videoRepository.findAllByCond(request.toDataRequest());

        return VideoPageResponse.of(
                videos,
                isPurchase(member, videos),
                isSubscribe(member, videos, request.isSubscribe()),
                getVideoUrls(videos),
                getVideoIdsInCart(member, videos.getContent())
        );
    }

    public VideoDetailResponse getVideo(Long loginMemberId, Long videoId) {

        Video video = verifiedVideoIncludeWithdrawal(videoId);

        Member loginMember = verifiedMemberOrNull(loginMemberId);

        Boolean isPurchased = isPurchased(loginMember, video);

        checkIfVideoClosed(isPurchased, video);

        return VideoDetailResponse.of(video,
                isSubscribed(loginMember, video),
                getAllUrls(video),
                isPurchased,
                isReplied(loginMember, video),
                isInCart(loginMember, video));
    }

    @Transactional
    public void watch(Long loginMemberId, Long videoId) {

        Video video = verifiedVideoIncludeWithdrawal(videoId);

        Member loginMember = verifiedMemberOrNull(loginMemberId);

        video.addView();
        if(loginMember == null) return;

        getOrCreateWatch(loginMember, video);
    }

    @Transactional
    public VideoCreateUrlResponse getVideoCreateUrl(Long loginMemberId, VideoCreateUrlServiceRequest request) {

        checkValidVideoName(request.getFileName());

        Member member = verifiedMemberWithChannel(loginMemberId);

        checkDuplicateVideoNameInSameChannel(loginMemberId, request.getFileName());

        Video video = Video.createVideo(
                member.getChannel(),
                request.getFileName()
        );

        videoRepository.save(video);

        String location = video.getVideoId() + "/" + request.getFileName();

        return VideoCreateUrlResponse.builder()
                .videoUrl(getUploadVideoUrl(loginMemberId, location))
                .thumbnailUrl(getUploadThumbnailUrl(loginMemberId, location, request.getImageType()))
                .build();
    }

    @Transactional
    public Long createVideo(Long loginMemberId, VideoCreateServiceRequest request) {

        Video video = verifiedVideo(loginMemberId, request.getVideoName());

        video.additionalCreateProcess(
                request.getPrice(),
                request.getDescription(),
                verifiedCategories(request.getCategories())
        );

        checkIfVideoUploaded(loginMemberId, video);

        return video.getVideoId();
    }

    @Transactional
    public void updateVideo(Long loginMemberId, VideoUpdateServiceRequest request) {

        Video video = verifiedVideo(loginMemberId, request.getVideoId());

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

        Video video = verifiedVideo(loginMemberId, videoId);

        video.close();
    }

    private void checkValidVideoName(String fileName) {
        if (fileName.contains("/")) {
            throw new VideoNameNotValidException("/");
        }
    }

    private List<Boolean> isPurchase(Member loginMember, Page<Video> videos) {

        if(loginMember == null) {
            return createBooleans(videos.getContent().size(), false);
        }

        List<Long> videoIds = videos.getContent().stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberPurchaseVideos(loginMember.getMemberId(), videoIds);
    }

    private List<Boolean> isSubscribe(Member loginMember, Page<Video> videos, boolean subscribe) {

        if(loginMember == null) {
            return createBooleans(videos.getContent().size(), false);
        }

        if (subscribe) {
            return createBooleans(videos.getContent().size(), true);
        }

        List<Long> memberIds = videos.stream()
                .map(Video::getMemberId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberSubscribeChannel(loginMember.getMemberId(), memberIds);
    }

    private List<Boolean> createBooleans(int size, boolean value) {
        return IntStream.range(0, size)
                .mapToObj(i -> value)
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> getVideoUrls(Page<Video> videos) {

        return videos.stream()
                .map(video -> {
                    Member member = video.getChannel().getMember();

                    Map<String, String> urls = new HashMap<>();

                    urls.put("thumbnailUrl", getThumbnailUrl(member.getMemberId(), video));
                    urls.put("imageUrl", getImageUrl(member));

                    return urls;
                })
                .collect(Collectors.toList());
    }

    private Map<String, String> getAllUrls(Video video) {

        Map<String, String> urls = new HashMap<>();

        Member owner = video.getChannel().getMember();

        urls.put("videoUrl", getVideoUrl(video, owner));
        urls.put("thumbnailUrl", getThumbnailUrl(owner.getMemberId(), video));
        urls.put("imageUrl", getImageUrl(owner));

        return urls;
    }

    private String getVideoUrl(Video video, Member owner) {
        return awsService.getFileUrl(owner.getMemberId(), video.getVideoFile(), FileType.VIDEO);
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

    private Boolean isInCart(Member member, Video video) {

        if(member == null) {
            return false;
        }

        return !getVideoIdsInCart(member, List.of(video)).isEmpty();
    }

    private List<Long> getVideoIdsInCart(Member member, List<Video> videos) {

        if(member == null) {
            return Collections.emptyList();
        }

        List<Long> videoIds = videos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return videoRepository.findVideoIdInCart(member.getMemberId(), videoIds);
    }

    private void checkIfVideoUploaded(Long loginMemberId, Video video) {
        boolean existVideo = awsService.isExistFile(loginMemberId, video.getVideoFile(), FileType.VIDEO);
        boolean existThumbnail = awsService.isExistFile(loginMemberId, video.getThumbnailFile(), FileType.THUMBNAIL);

        if(!existVideo) {
            throw new VideoNotUploadedException(video.getVideoName());
        }

        if(!existThumbnail) {
            throw new ThumbnailNotUploadedException(video.getVideoName());
        }
    }

    private Member verifiedMemberOrNull(Long loginMemberId) {

        if(loginMemberId == null || loginMemberId == -1) {
            return null;
        }

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

    private Video verifiedVideo(Long memberId, Long videoId) {
        Video video = videoRepository.findVideoDetail(videoId)
                .orElseThrow(VideoNotFoundException::new);

        if(!video.getChannel().getMember().getMemberId().equals(memberId)) {
            throw new VideoAccessDeniedException();
        }

        return video;
    }

    private Video verifiedVideo(Long memberId, String videoName) {

        return videoRepository.findVideoByNameWithMember(memberId, videoName)
                .orElseThrow(VideoNotFoundException::new);
    }

    private Video verifiedVideoIncludeWithdrawal(Long videoId) {
        return videoRepository.findVideoDetailIncludeWithdrawal(videoId)
                .orElseThrow(VideoNotFoundException::new);
    }

    private void checkDuplicateVideoNameInSameChannel(Long memberId, String videoName) {

        videoRepository.findVideoByNameWithMember(memberId, videoName)
                .ifPresent(video -> {
                    throw new VideoNameDuplicateException();
                });
    }

    private void checkIfVideoClosed(boolean isPurchased, Video video) {
        if(!isPurchased && video.getVideoStatus().equals(VideoStatus.CLOSED)) {
            throw new VideoClosedException(video.getVideoName());
        }
    }

    private void getOrCreateWatch(Member member, Video video) {
        Watch watch = getWatch(member, video)
                .orElseGet(
                        () -> createWatch(member, video)
                );

        watch.setLastWatchedTime(LocalDateTime.now());
    }

    private Optional<Watch> getWatch(Member member, Video video) {
        return watchRepository.findByMemberAndVideo(member, video);
    }

    private Watch createWatch(Member member, Video video) {
        return watchRepository.save(Watch.createWatch(member, video));
    }

    private Boolean isPurchased(Member member, Video video) {

        if(member == null) return false;

        return videoRepository.isPurchased(member.getMemberId(), video.getVideoId());
    }

    private Boolean isReplied(Member member, Video video) {

        if(member == null) return false;

        return videoRepository.isReplied(member.getMemberId(), video.getVideoId());
    }

    private Boolean isSubscribed(Member member, Video video) {

        if(member == null) return false;

        if(video.getChannel() == null) return false;

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

    private boolean createCart(Member member, Video video) {

        if(video.getVideoStatus().equals(VideoStatus.CLOSED)) {
            throw new VideoClosedException(video.getVideoName());
        }

        cartRepository.save(Cart.createCart(member, video, video.getPrice()));
        return true;
    }

    private boolean deleteCart(Cart cart) {
        cartRepository.delete(cart);
        return false;
    }
}
