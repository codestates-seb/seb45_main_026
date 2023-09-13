import React,{useEffect, useState} from "react";
import tokens from "../../styles/tokens.json";
import { styled } from "styled-components";
import arrowDown from "../../assets/images/icons/arrow/subscribe_arrow_down.svg";
import arrowUp from "../../assets/images/icons/arrow/subscribe_arrow_up.svg";
import HorizonItem from "./HorizonItem";
import axios from "axios";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useToken } from "../../hooks/useToken";
import { BodyTextTypo, Heading5Typo } from "../../atoms/typographys/Typographys";
import { useInView } from "react-intersection-observer";

const globalTokens = tokens.global;

const ItemBody = styled.div`
    width: 95%;
    padding: ${globalTokens.Spacing28.value}px;
    gap: ${globalTokens.Spacing8.value}px;
    border: 1px ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value} solid;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
`
const ProfileContainer = styled.div`
    height: 50px;
    max-width: 250px;
    display: flex;
    flex-direction: row;
    align-items: center;   
    gap: ${globalTokens.Spacing8.value}px;
    &:hover{
        cursor: pointer;
    }
`
const ProfileImg = styled.img`
    object-fit: cover;
    height: 100%;
    width: 100%;
`
const ImgContainer = styled.span`
    width: 50px;
    height: 50px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid lightgray;
    overflow: hidden;
`
const TextInfor = styled.div`
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
`
const AuthorName = styled(Heading5Typo)`
`
const Subscribers = styled(BodyTextTypo)`
`
const ContentContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 12px 0;
`
const TopContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
`
const LectureCount = styled(BodyTextTypo)`
    display: flex;
`
const CountNum = styled(BodyTextTypo)`
    font-weight: ${globalTokens.Bold.value};
`
const AccordionButton = styled.button`
    width: 40px;
    height: 20px;
`
const AccordionArrow = styled.img`
    width: 35px;
    height: 35px;
`
const HorizonItemContainer = styled.ul`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: ${globalTokens.Spacing16.value}px;
    margin-bottom: ${globalTokens.Spacing28.value}px;
    /* max-height: ${(props)=>props.isOpen?'10000px':'0px'};
    overflow: hidden; */
    max-height: 1000px;
    transition: 500ms;
`
const BottomDiv = styled.div`
  height: 2px;
  width: 10px;
`;


export default function PurchasedItem({ channel, setChannelList }) {
  
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const refreshToken = useToken();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false)
  const [page, setPage] = useState(1);
  const [maxPage, setMaxPage] = useState(null);
  const [bottomRef, bottomInView] = useInView();
  const [loading,setLoading]=useState(true)
  
  const arccordionHandler = (memberId) => {
        setIsOpen(!isOpen);
        axios
          .get(
            `https://api.itprometheus.net/members/playlists/channels/details?member-id=${memberId}&page=1`,
            {
              headers: { Authorization: accessToken.authorization },
            }
          )
            .then((res) => {
                if (!isOpen) {
                    setChannelList(prev => prev.map((el) => {
                        if (el.memberId === memberId) {
                            return {...el,list:res.data.data}
                        } else {
                            return el
                        }
                    }))
                  setMaxPage(res.data.pageInfo.totalPage);
                  setLoading(false)
                } else {
                    setChannelList(prev => prev.map((el) => {
                        if (el.memberId === memberId) {
                            return {...el,list:[]}
                        } else {
                            return el
                        }
                    }))
              }
          })
          .catch((err) => {
            if(err.response.data.message==='만료된 토큰입니다.') {
              refreshToken(()=>{ arccordionHandler(channel.memberId) });
            } else {
              console.log(err);
            }
          });
    }
  useEffect(() => {
    if (channel.list !== [] && page !== 1) {
      axios
        .get(
          `https://api.itprometheus.net/members/playlists/channels/details?member-id=${channel.memberId}&page=${page}`,
          {
            headers: { Authorization: accessToken.authorization },
          }
        )
        .then((res) => {
          if (!isOpen) {
            setChannelList((prev) =>
              prev.map((el) => {
                if (el.memberId === channel.memberId) {
                  return { ...el, list: [...el.list,...res.data.data] };
                } else {
                  return el;
                }
              })
            );
            setLoading(false);
          } 
        })
        .catch((err) => {
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken(() => {
              arccordionHandler(channel.memberId);
            });
          } else {
            console.log(err);
          }
        });
    }
  }, [page])

  useEffect(() => {
    if (bottomInView && maxPage && page < maxPage) {
      setLoading(true);
      setPage(page + 1);
    }
  }, [bottomInView]);

    return (
      <ItemBody isDark={isDark}>
        <ProfileContainer isDark={isDark} onClick={()=>navigate(`/channels/${channel.memberId}`)} >
          <ImgContainer>
            <ProfileImg src={channel.imageUrl} />
          </ImgContainer>
          <TextInfor>
            <AuthorName isDark={isDark}>{channel.channelName}</AuthorName>
            <Subscribers isDark={isDark}>구독자 {channel.subscribers} 명</Subscribers>
          </TextInfor>
        </ProfileContainer>
        <TopContainer>
          <LectureCount isDark={isDark}>
            <CountNum isDark={isDark}>{channel.videoCount}</CountNum>개의 강의
          </LectureCount>
          <AccordionButton onClick={() => arccordionHandler(channel.memberId)}>
            <AccordionArrow src={isOpen ? arrowUp : arrowDown} />
          </AccordionButton>
        </TopContainer>
        <ContentContainer>
          <HorizonItemContainer >
            {channel.list!==[]?channel.list.map(el=><HorizonItem key={el.videoId} lecture={el} channel={channel} />):<></>}
            {page < maxPage&&!loading&&isOpen?<BottomDiv ref={bottomRef}/>:<></>}
          </HorizonItemContainer>
        </ContentContainer>
      </ItemBody>
    );
}