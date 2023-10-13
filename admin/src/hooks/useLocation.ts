const Url: string = "https://www.itprometheus.net";

export const useLocate = () => {
  const locateVideo = (videoId: number): void => {
    window.location.assign(`${Url}/videos/${videoId}`);
  };

  const locateReview = (videoId: number, reportId: number): void => {
    window.location.assign(`${Url}/videos/${videoId}#${reportId}`);
  };

  const locateChannel = (memberId: number): void => {
    window.location.assign(`${Url}/channels/${memberId}`);
  };

  const locateNotice = (memberId: number, announcementId: number): void => {
    window.location.assign(`${Url}/channels/${memberId}#${announcementId}`);
  };

  return { locateVideo, locateReview, locateChannel, locateNotice };
};
