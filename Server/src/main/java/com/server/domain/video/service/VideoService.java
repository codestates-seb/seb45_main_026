package com.server.domain.video.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.report.entity.VideoReport;
import com.server.domain.report.service.ReportService;
import com.server.domain.report.service.dto.response.ReportDetailResponse;
import com.server.domain.report.service.dto.response.VideoReportResponse;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.video.repository.dto.response.VideoReportData;
import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.video.service.dto.response.*;
import com.server.domain.watch.entity.Watch;
import com.server.domain.watch.repository.WatchRepository;
import com.server.global.exception.businessexception.categoryexception.CategoryNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.videoexception.*;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ReportService reportService;

    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository,
                        WatchRepository watchRepository, CategoryRepository categoryRepository,
                        CartRepository cartRepository, AwsService awsService, ReportService reportService) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
        this.watchRepository = watchRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.awsService = awsService;
        this.reportService = reportService;
    }

    public Page<VideoPageResponse> getVideos(VideoGetServiceRequest request) {

        Long memberId = verifiedMemberIdOrNull(request.getLoginMemberId());

        Page<Video> videos = videoRepository.findAllByCond(request.toDataRequest());

        return VideoPageResponse.of(
                videos,
                isPurchase(memberId, videos),
                isSubscribe(memberId, videos, request.isSubscribe()),
                getVideoUrls(videos),
                getVideoIdsInCart(memberId, videos.getContent())
        );
    }

    public Page<VideoPageResponse> searchVideos(String keyword, VideoGetServiceRequest request) {

        Long memberId = verifiedMemberIdOrNull(request.getLoginMemberId());

        Page<Video> videos = videoRepository.findAllByCond(keyword, request.toDataRequest());

        return VideoPageResponse.of(
                videos,
                isPurchase(memberId, videos),
                isSubscribe(memberId, videos, request.isSubscribe()),
                getVideoUrls(videos),
                getVideoIdsInCart(memberId, videos.getContent())
        );
    }

    public VideoDetailResponse getVideo(Long loginMemberId, Long videoId) {

        Video video = verifiedVideoIncludeWithdrawal(videoId);

        Member member = verifiedMemberOrNull(loginMemberId);

        Long memberId = member == null ? null : member.getMemberId();

        Boolean isPurchased = isPurchased(memberId, video.getVideoId());

        if(member != null && member.isAdmin()) isPurchased = true;

        checkIfVideoClosed(isPurchased, video);

        return VideoDetailResponse.of(video,
                isSubscribed(memberId, video),
                getAllUrls(video),
                isPurchased,
                isReplied(memberId, video),
                isInCart(memberId, video));
    }

    public PreviewUrlResponse getPreviewUrl(Long videoId) {

        String videoFile = videoRepository.findPreviewUrlByVideoId(videoId);

        String previewUrl = awsService.getFileUrl(videoFile, FileType.PREVIEW);

        return PreviewUrlResponse.of(previewUrl);
    }

    public VideoUrlResponse getVideoUrl(Long memberId, Long videoId) {

        Boolean isPurchased = isPurchased(memberId, videoId);

        if(isAdmin()) isPurchased = true;

        if(!isPurchased) throw new VideoNotPurchasedException();

        String videoFile = videoRepository.findVideoUrlByVideoId(videoId);

        String videoUrl = awsService.getFileUrl(videoFile, FileType.VIDEO);

        return VideoUrlResponse.of(videoUrl);
    }

    @Transactional
    public void watch(Long loginMemberId, Long videoId) {

        //getVideo 를 readOnly 로 하기 위해 따로 조회

        Video video = verifiedVideoIncludeWithdrawal(videoId);

        Long memberId = verifiedMemberIdOrNull(loginMemberId);

        video.addView();
        if(memberId == null) return;

        getOrCreateWatch(memberId, video);
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
                .previewUrl(getUploadPreviewUrl(loginMemberId, location))
                .thumbnailUrl(getUploadThumbnailUrl(loginMemberId, location, request.getImageType()))
                .build();
    }

    @Transactional
    public Long createVideo(Long loginMemberId, VideoCreateServiceRequest request) {

        Video video = verifiedVideo(loginMemberId, request.getVideoName());

        video.additionalCreateProcess(
                request.getPrice(),
                request.getDescription(),
                verifiedCategories(request.getCategories()),
                request.isHasPreview()
        );

        checkIfVideoUploaded(video);

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
    public boolean changeVideoStatus(Long loginMemberId, Long videoId) {

        Video video = verifiedVideo(loginMemberId, videoId);

        if(isAdmin()) {
            return changeVideoStatusAdmin(video);
        }

        return changeVideoStatusMember(video);
    }

    private boolean changeVideoStatusAdmin(Video video) {

        if(video.isClosed() || video.isAdminClosed()) {
            video.open();
            return true;
        }

        video.adminClose();
        return false;
    }

    private boolean changeVideoStatusMember(Video video) {

        if(video.isAdminClosed()) {
            throw new VideoAdminClosedException();
        }

        if(video.isClosed()) {
            video.open();
            return true;
        }

        video.close();
        return false;
    }

    @Transactional
    public boolean reportVideo(Long loginMemberId, Long videoId, String reportContent) {

        Video video = verifiedVideoIncludeWithdrawal(videoId);

        Member reporter = verifiedMember(loginMemberId);

        return reportService.reportVideo(reporter, video, reportContent);
    }

    public Page<VideoReportResponse> getVideoReports(int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        Page<VideoReportData> videoReportData = videoRepository.findVideoReportDataByCond(pageable, sort);

        return videoReportData.map(VideoReportResponse::of);
    }

    public Page<ReportDetailResponse> getReports(Long videoId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<VideoReport> reports = videoRepository.findReportsByVideoId(videoId, pageable);

        return reports.map(ReportDetailResponse::of);
    }

    private void checkValidVideoName(String fileName) {
        if (fileName.contains("/")) {
            throw new VideoNameNotValidException("/");
        }
    }

    private List<Boolean> isPurchase(Long memberId, Page<Video> videos) {

        if(memberId == null) {
            return createBooleans(videos.getContent().size(), false);
        }

        List<Long> videoIds = videos.getContent().stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberPurchaseVideos(memberId, videoIds);
    }

    private List<Boolean> isSubscribe(Long memberId, Page<Video> videos, boolean subscribe) {

        if(memberId == null) {
            return createBooleans(videos.getContent().size(), false);
        }

        if (subscribe) {
            return createBooleans(videos.getContent().size(), true);
        }

        List<Long> memberIds = videos.stream()
                .map(Video::getMemberId)
                .collect(Collectors.toList());

        return memberRepository.checkMemberSubscribeChannel(memberId, memberIds);
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

                    urls.put("thumbnailUrl", getThumbnailUrl(video));
                    urls.put("imageUrl", getImageUrl(member));

                    return urls;
                })
                .collect(Collectors.toList());
    }

    private Map<String, String> getAllUrls(Video video) {

        Map<String, String> urls = new HashMap<>();

        Member owner = video.getChannel() == null ? null : video.getChannel().getMember();

        urls.put("previewUrl", getPreviewUrl(video));
        urls.put("thumbnailUrl", getThumbnailUrl(video));
        urls.put("imageUrl", getImageUrl(owner));

        return urls;
    }

    private String getVideoUrl(Video video) {
        return awsService.getFileUrl(video.getVideoFile(), FileType.VIDEO);
    }

    private String getImageUrl(Member member) {

        if(member == null) {
            return "삭제된 채널";
        }

        return awsService.getFileUrl(member.getImageFile(), FileType.PROFILE_IMAGE);
    }

    private String getThumbnailUrl(Video video) {
        return awsService.getFileUrl(video.getThumbnailFile(), FileType.THUMBNAIL);
    }

    private String getPreviewUrl(Video video) {
        return awsService.getFileUrl(video.getPreviewFile(), FileType.PREVIEW);
    }


    private String getUploadVideoUrl(Long loginMemberId, String location) {
        return awsService.getUploadVideoUrl(loginMemberId, location);
    }

    private String getUploadPreviewUrl(Long loginMemberId, String location) {
        return awsService.getPublicUploadUrl(loginMemberId, location, FileType.PREVIEW, null);
    }

    private String getUploadThumbnailUrl(Long loginMemberId, String location, ImageType imageType) {
        return awsService.getPublicUploadUrl(loginMemberId,
                location,
                FileType.THUMBNAIL,
                imageType);
    }

    private Boolean isInCart(Long memberId, Video video) {

        if(memberId == null) {
            return false;
        }

        return !getVideoIdsInCart(memberId, List.of(video)).isEmpty();
    }

    private List<Long> getVideoIdsInCart(Long memberId, List<Video> videos) {

        if(memberId == null) {
            return Collections.emptyList();
        }

        List<Long> videoIds = videos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        return videoRepository.findVideoIdInCart(memberId, videoIds);
    }

    private void checkIfVideoUploaded(Video video) {
        boolean existVideo = awsService.isExistFile(video.getVideoFile(), FileType.VIDEO);
        boolean existThumbnail = awsService.isExistFile(video.getThumbnailFile(), FileType.THUMBNAIL);

        if(!existVideo) {
            throw new VideoNotUploadedException(video.getVideoName());
        }

        if(!existThumbnail) {
            throw new ThumbnailNotUploadedException(video.getVideoName());
        }

        if(video.getPreviewFile() != null) {
            boolean existPreview = awsService.isExistFile(video.getPreviewFile(), FileType.PREVIEW);
            if(!existPreview) {
                throw new PreviewNotUploadedException(video.getVideoName());
            }
        }

    }

    private Long verifiedMemberIdOrNull(Long loginMemberId) {

        if(loginMemberId == null || loginMemberId == -1) {
            return null;
        }

        return memberRepository.findMemberIdById(loginMemberId);
    }

    private Member verifiedMemberOrNull(Long loginMemberId) {

        if(loginMemberId == null || loginMemberId == -1) {
            return null;
        }

        return memberRepository.findById(loginMemberId).orElse(null);
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

        Video video = videoRepository.findVideoDetailIncludeWithdrawal(videoId)
                .orElseThrow(VideoNotFoundException::new);

        if(isAdmin()) {
            return video;
        }

        if(video.getMemberId() == null || !video.getMemberId().equals(memberId)) {
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

    private void getOrCreateWatch(Long memberId, Video video) {
        Watch watch = getWatch(memberId, video)
                .orElseGet(
                        () -> createWatch(memberId, video)
                );

        watch.setLastWatchedTime(LocalDateTime.now());
    }

    private Optional<Watch> getWatch(Long memberId, Video video) {
        return watchRepository.findByMemberAndVideo(memberId, video.getVideoId());
    }

    private Watch createWatch(Long memberId, Video video) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return watchRepository.save(Watch.createWatch(member, video));
    }

    private Boolean isPurchased(Long memberId, Long videoId) {

        if(memberId == null) return false;

        return videoRepository.isPurchased(memberId, videoId);
    }

    private Boolean isReplied(Long memberId, Video video) {

        if(memberId == null) return false;

        return videoRepository.isReplied(memberId, video.getVideoId());
    }

    private Boolean isSubscribed(Long memberId, Video video) {

        if(memberId == null) return false;

        if(video.getChannel() == null) return false;

        return memberRepository.checkMemberSubscribeChannel(
                memberId,
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

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
