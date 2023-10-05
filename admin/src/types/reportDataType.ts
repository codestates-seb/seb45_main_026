export type reportVideoDataType = {
  videoId: number;
  videoName: string;
  videoStatus: string;
  reportCount: number;
  lastReportedDate: string;
  createdDate: string;
};

export type videoReportListType = {
  reportId: number;
  reportContent: string;
  memberId: number;
  nickname: string;
  email: string;
  createdDate: string;
};

export type reportReviewDataType = {
  videoId: number;
  videoName: string;
  replyId: number;
  content: string;
  reportCount: number;
  createdDate: string;
  lastReportedDate: string;
};

export type reviewReportListType = {
  reportId: number;
  nickname: string;
  reportContent: string;
  createdDate: string;
  memberId: number;
  email: string;
};
