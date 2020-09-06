import { Effect } from 'dva';
import { Reducer } from 'redux';

import {
  RelatedKeywordsType,
  SentimentDistributionType,
  HottestQuestionsType,
  MostVotedAnswersType,
  AuthorsFrequencyType,
  MostLikedAuthorsType,
  TrendType,
  GenderDistributionType,
  MostFollowedUsersType,
  LocationDistributionType,
  BusinessDistributionType,
  CompanyDistributionType,
  JobDistributionType,
  SchoolDistributionType,
  UsersPortraitType,
} from './analysis'

import {requestAnswersWithKeywordWithinTimeAnalysis} from '@/services/analysis'

export interface AnswersWithKeywordWithinTimeAnalysisResultType {
  keywords:Array<string>;
  answersNum: number;
  anonymousUsersNum: number;
  questionsNum: number;
  relatedKeywords: RelatedKeywordsType;
  sentimentDistribution: SentimentDistributionType;
  hottestQuestions: HottestQuestionsType;
  mostVotedAnswers: MostVotedAnswersType;
  highFrequencyAuthors: AuthorsFrequencyType;
  mostLikedAuthors: MostLikedAuthorsType;
  sentimentTrend: TrendType;
  heatTrend: TrendType;
  genderDistribution: GenderDistributionType;
  mostFollowedUsers: MostFollowedUsersType;
  locationDistribution: LocationDistributionType;
  businessDistribution: BusinessDistributionType;
  companyDistribution: CompanyDistributionType;
  jobDistribution: JobDistributionType;
  schoolDistribution: SchoolDistributionType;
  usersPortrait: UsersPortraitType;
}

export interface AnswersWithKeywordWithinTimeAnalysisStateType {
  loading: boolean;
  errorMsg?: string;
  analysisResult?: AnswersWithKeywordWithinTimeAnalysisResultType;
  timeCosts: number;
  begin: string;
  end: string;
  text: string;
}

//TODO::this should be a pure component
export interface AnswersWithKeywordWithinTimeAnalysisModelType {
  namespace: string
  state: AnswersWithKeywordWithinTimeAnalysisStateType;
  effects: {
    fetch: Effect;//TODO::remove latter
  };
  reducers: {
    loadAnalysisState: Reducer<AnswersWithKeywordWithinTimeAnalysisStateType>//TODO::remove latter
  };
}

const AnswersWithKeywordWithinTimeAnalysis: AnswersWithKeywordWithinTimeAnalysisModelType = {
  namespace: 'answersWithKeywordWithinTimeAnalysis',
  state: {
    loading: false,
    timeCosts: 0,
    begin: '',
    end: '',
    text: ''
  },

  effects: {
    *fetch({payload}, { call, put }) {
      yield put({
        type: 'setLoading',
        payload: {loading:true},
      });
      const response = yield call(requestAnswersWithKeywordWithinTimeAnalysis, payload);
      //console.log(`fetch: `,payload)
      yield put({
        type: 'loadAnalysisState',
        payload: response,
      });
    },
  },

  reducers: {
    loadAnalysisState(state:AnswersWithKeywordWithinTimeAnalysisStateType , action:any) {
      console.log(`loadAnalysisState:`,action.payload)
      const {payload} = action //console.log(payload)
      const {success,message } = payload
      if(success) {
        return {
          ...state,
          loading: false,
          errorMsg:null,
          inputVisible:false,
          ...payload
        };
      } else { //false
        return {
          ...state,
          loading: false,
          inputVisible:true,
          errorMsg:message
        };
      }
    },

    setLoading(state:AnswersWithKeywordWithinTimeAnalysisStateType , action:any) {
      console.log(`setLoading:`,action.payload)
      const {payload} = action
      const {loading } = payload
      return {
        ...state,
        loading
      };
    },



  },
};

export default AnswersWithKeywordWithinTimeAnalysis;