//basic entity data type

export interface ZhihuUserType {
  id: string;
  avatarUrl: string;
  followerCount: number;
  gender: number;
  headline: string;
  name: string;
  urlToken: string;
  userType: string;
  description: string;
  location: string;
  company: string;
  job: string;
  business: string;
  school: string;
  major: string;
}

export interface ZhihuQuestionType {
  id: number;
  title: string;
  updatedTime: number;
  createdTime: number;
}

export interface ZhihuAnswerType {
  id:number;
  questionId:number;
  createdTime:number;
  updatedTime:number;
  voteUpCount:number;
  commentCount:number;
  content:string;
  authorUrlToken:string;
  authorId:string;
  sentiment:number;
}

export interface KeywordsItemType {
  word: string;
  count: number;
}

export type KeywordsType = Array<KeywordsItemType>

export type RelatedKeywordsType = Array<KeywordsItemType>

export interface SentimentDistributionItemType {
  type: number;
  count: number;
}

export type SentimentDistributionType = Array<SentimentDistributionItemType>


export interface HottestQuestionsItemType {
  question: ZhihuQuestionType;
  answersNum: number
}

export type HottestQuestionsType = Array<HottestQuestionsItemType>

export interface MostVotedAnswersItemType {
  answer: ZhihuAnswerType;
  user: ZhihuUserType;
  question: ZhihuQuestionType;
}

export type MostVotedAnswersType = Array<MostVotedAnswersItemType>

export interface AuthorsFrequencyItemType {
  user: ZhihuUserType;
  times: number;
}

export type AuthorsFrequencyType = Array<AuthorsFrequencyItemType>


export interface MostLikedAuthorsItemType {
  user: ZhihuUserType;
  count: number;
}

export type MostLikedAuthorsType = Array<MostLikedAuthorsItemType>


export interface TrendItemType {
  time: string;
  value: number
}

export type HourTrendType = Array<TrendItemType>
export type DayTrendType = Array<TrendItemType>
export type DayPredictType = Array<TrendItemType>
export interface TrendType {
  hourTrend: HourTrendType;
  dayTrend: DayTrendType;
  dayPredict: DayPredictType;
} 

export interface GenderDistributionItemType {
  type: number;
  count: number;
}

export type GenderDistributionType = Array<GenderDistributionItemType>

export type MostFollowedUsersType = Array<ZhihuUserType>

export interface LocationDistributionItemType {
  name: string;
  count: number;
}

export type LocationDistributionType = Array<LocationDistributionItemType>

export interface BusinessDistributionItemType {
  name: string;
  count: number;
}

export type BusinessDistributionType = Array<BusinessDistributionItemType>

export interface CompanyDistributionItemType {
  name: string;
  count: number;
}

export type CompanyDistributionType = Array<CompanyDistributionItemType>

export interface JobDistributionItemType {
  name: string;
  count: number;
}

export type JobDistributionType = Array<JobDistributionItemType>

export interface SchoolDistributionItemType {
  name: string;
  count: number;
}

export type SchoolDistributionType = Array<SchoolDistributionItemType>

export type UsersPortraitType = Array<KeywordsItemType>

