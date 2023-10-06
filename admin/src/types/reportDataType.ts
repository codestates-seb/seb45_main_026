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

export type ReportChannelDataType = {
  memberId: number;
  channelName: string;
  memberStatus: string;
  blockReason: string;
  blockEndDate: string;
  reportCount: number;
  createdDate: string;
  lastReportedDate: string;
};

export type ChannelReportListType = {
  reportId: number;
  reportContent: string;
  createdDate: string;
  memberId: number;
  email: string;
  nickname: string;
};

export type ReportNoticeDataType = {
  announcementId: number;
  content: string;
  memberId: number;
  reportCount: number;
  createdDate: string;
  lastReportedDate: string;
};

export type NoticeReportListType = {
  reportId: number;
  reportContent: string;
  createdDate: string;
  memberId: number;
  email: string;
  nickname: string;
};

export type UseLocateType = {
  locateVideo?(videoId: number): void;
  locateReview?(videoId: number, reportId: number): void;
  locateChannel?(videoId: number): void;
  locateNotice?(memberId: number, announcementId: number): void;
};

export interface DarkMode {
  isDark: boolean;
}