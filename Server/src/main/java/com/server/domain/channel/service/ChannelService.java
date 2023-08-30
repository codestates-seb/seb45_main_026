package com.server.domain.channel.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.channel.service.dto.ChannelDto;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.PageInfo;
import com.server.module.s3.service.AwsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final AwsService awsService;
    private final MemberRepository memberRepository;
    private final  OrderRepository orderRepository;


    public ChannelService(ChannelRepository channelRepository,
                          VideoRepository videoRepository,
                          AwsService awsService,
                          MemberRepository memberRepository,
                          SubscribeRepository subscribeRepository,
                          OrderRepository orderRepository) {

        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.awsService = awsService;
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;

    }

    //memberId 받는 걸로 수정 , 구독햇는지 여부 확인
    @Transactional(readOnly = true)
    public ChannelDto.ChannelInfo getChannelInfo(Long memberId, Long channelId) {
        Channel channel = channelRepository.findByChannelId(channelId);

        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        boolean isSubscribed = false;
        boolean hasPurchasedVideos = false;

        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            isSubscribed = isSubscribed(memberId, channel);
            hasPurchasedVideos = hasPurchasedVideos(memberId, channel);
        }

        ChannelDto.ChannelInfo channelInfo = ChannelDto.ChannelInfo.builder()
                .memberId(channel.getChannelId())
                .channelName(channel.getChannelName())
                .subscribers(channel.getSubscribers())
                .isSubscribed(isSubscribed)
                .isPurchaseVideos(hasPurchasedVideos)
                .description(channel.getDescription())
                .imageUrl(awsService.getImageUrl("name"))
                .createdDate(channel.getCreatedDate())
                .build();

        return channelInfo;
    }


    private boolean isSubscribed(Long memberId, Channel channel){
       Member member = memberRepository.findById(memberId)
               .orElseThrow(MemberNotFoundException::new);
       List<Subscribe> subscribeList = member.getSubscribes();

       for(Subscribe subscribe : subscribeList){
           if(subscribe.getChannel().equals(channel)){
               return true;
           }
       }
       return false;
    }

    public boolean hasPurchasedVideos(Long memberId, Channel channel) {
        return videoRepository.findVideosBy(memberId, channel);
    }







    //로그인했는지 여부(로그인을해야 내 채널을 업데이트 할 수 잇으니까) , 합치기, //memberId의 채널로 가지고 와야함,
    //구매한 내 동영상 정보를 업데이트 하ㅐ는 것
    //이미 로그인한 회원만 수정버튼 보이는거 아님?

    public void updatePurchasedVideoInfo(Long memberId, Long videoId, VideoUpdateServiceRequest updateInfo) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new); //내가 구매한 동영상인지, 동영상 소유자인지 확인해야 하니까

        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);//이 부분 memberId의 비디오 채널로 가지고 와야함

        if (!isPurchasedVideo(memberId, video) || !isVideoOwner(memberId, video)) {
            throw new MemberAccessDeniedException();
        }

        videoRepository.save(video);
    }

    public boolean isPurchasedVideo(Long memberId, Video video) { //구매여부
        List<Video> OrderVideos = videoRepository.findVideoBy(memberId, video);
        return OrderVideos.contains(video);
    }

    public boolean isVideoOwner(Long memberId, Video video) { //소유자 확인
        Channel ownerChannel = video.getChannel();
        return ownerChannel.getMember().getMemberId().equals(memberId);
    }




    // 구독 여부 업데이트
    public boolean updateSubscribe(Long memberId, Long loginMemberId) {

        Member member = memberRepository.findById(memberId) //업데이트하려면 일단.. 채널이 있어야함 채널을 찾으려면.. 멤버가 있어야함
                .orElseThrow(MemberNotFoundException::new);

        Member loginMember = memberRepository.findById(loginMemberId)  //로그인했는지.. 확인하는데 왜 하냐면 로그인한 사람이 구독한 채널을 업데이트 해야하니까
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = member.getChannel();

        if (!memberId.equals(loginMemberId)) {
            throw new MemberAccessDeniedException();
        }

        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        // 로그인한 사용자가 이미 구독 중인지 여부 확인
        boolean isSubscribed = isSubscribed(channel, loginMember);

        if (isSubscribed) {
            unsubscribe(loginMember, channel);
            return false;
        } else {
            subscribe(loginMember, channel);
            return true;
        }
    }

    // 구독 여부 확인
    private boolean isSubscribed(Channel channel, Member member) {
        return member.getSubscribes().contains(channel);
    }

    // 구독...?..
    private void subscribe(Member member, Channel channel) {
        Subscribe subscribe = new Subscribe(member, channel);
        channel.getSubscribes().add(subscribe);
        channel.setSubscribers(channel.getSubscribers() + 1); // 구독자 수 증가
    }

    // 구독 취소
    private void unsubscribe(Member member, Channel channel) {
        List<Subscribe> subscribeList = channel.getSubscribes();
        for (Subscribe subscribe : subscribeList) {
            if (subscribe.getMember().equals(member)) {
                subscribeList.remove(subscribe);
                channel.setSubscribers(channel.getSubscribers() - 1); // 구독자 수 감소
                break;
            }
        }
    }








    //전체채널 조회,
    //내가 구매한 동영상인지도 검증 필요
    public ApiPageResponse<ChannelDto.ChannelVideoResponseDto> getChannelVideos(
            Long loggedInMemberId,
            Long memberId,
            int page,
            Sort sort) {

        Member member = memberRepository.findById(memberId) //일단 member를 찾는다
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = member.getChannel(); //멤버에서 채널을 찾는다
        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.Direction.DESC, String.valueOf(sort));
        Page<Video> videoPage = videoRepository.findByChannel(channel, pageRequest);

        List<ChannelDto.ChannelVideoResponseDto> videoResponses = new ArrayList<>();
        for (Video video : videoPage.getContent()) {
            String imageUrl = awsService.getImageUrl(member.getImageFile());


            boolean isPurchased = checkPurchase(loggedInMemberId, video);

            if (!isPurchased) {
                continue;
            }
            List<String> categoryNames = new ArrayList<>();
            List<VideoCategory> categories = video.getVideoCategories();
//            for (Category category : categories) {
//                categoryNames.add(category.getCategoryName());
//            }


        }

        PageInfo pageInfo = PageInfo.of(videoPage);

        return new ApiPageResponse<>(
                videoResponses,
                pageInfo,
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "GoodLuck"
        );
    }


    //구매햇는지 여부 확인
    private boolean checkPurchase(Long memberId, Video video) {
        video.getOrderVideos().contains(memberId);
        return true;
    }



    public void createChannel(Member signMember) {
        Channel channel = Channel.createChannel(signMember.getNickname());

        channel.setMember(signMember);
        channelRepository.save(channel);
    }


//    boolean isLoggedInMember(Long loginMemberId, Long memberId) {
//        Member Member = memberRepository.findById(memberId).orElse(null);
//        if (Member != null) {
//            return loginMemberId.equals(Member.getMemberId());
//        }
//        return false;
//    }

}
