import { Effect } from 'dva';
import { Reducer } from 'redux';

import {
  RelatedKeywordsType,
  SentimentDistributionType,
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
  ZhihuQuestionType,
} from './analysis'

import {requestAnswersOfQuestionsAnalysis,requestSearchQuestionesByKeywords} from '@/services/analysis'

export interface AnswersOfQuestionsAnalysisResultType {
  questions:Array<ZhihuQuestionType>;
  answersNum: number;
  anonymousUsersNum: number;
  questionsNum: number;
  keywords: RelatedKeywordsType;
  sentimentDistribution: SentimentDistributionType;
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

export interface AnswersOfQuestionsAnalysisStateType {
  candidateQuestions?:Array<ZhihuQuestionType>;
  loading: boolean;
  errorMsg?: string;
  analysisResult?: AnswersOfQuestionsAnalysisResultType;
  timeCosts: number;
}

//TODO::this should be a pure component
export interface AnswersOfQuestionsAnalysisModelType {
  namespace: string
  state: AnswersOfQuestionsAnalysisStateType;
  effects: {
    fetch: Effect;
    loadCandidateQuestions:Effect;
  };
  reducers: {
    loadAnalysisState: Reducer<AnswersOfQuestionsAnalysisStateType>
    setCandicateQuestions: Reducer<AnswersOfQuestionsAnalysisStateType>
    setLoading: Reducer<AnswersOfQuestionsAnalysisStateType>
  };
}

const AnswersOfQuestionsAnalysisModel: AnswersOfQuestionsAnalysisModelType = {
  namespace: 'answersOfQuestionsAnalysis',
  state: {
    loading: false,
    timeCosts: 0
  },

  effects: {
    *loadCandidateQuestions({payload}, { call, put }) { //载入候选问题
      yield put({
        type: 'setLoading',
        payload: {loading:true},
      });
      const response = yield call(requestSearchQuestionesByKeywords, payload);
      //console.log(`fetch: `,payload)
      yield put({
        type: 'setCandicateQuestions',
        payload: response,
      });
    },
    *fetch({payload}, { call, put }) { //载入分析数据
      yield put({
        type: 'setLoading',
        payload: {loading:true},
      });
      const response = yield call(requestAnswersOfQuestionsAnalysis, payload);
      //console.log(`fetch: `,payload)
      yield put({
        type: 'loadAnalysisState',
        payload: response,
      });
    },
  },

  reducers: {
    loadAnalysisState(state:AnswersOfQuestionsAnalysisStateType , action:any) {
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

    setLoading(state:AnswersOfQuestionsAnalysisStateType , action:any) {
      console.log(`setLoading:`,action.payload)
      const {payload} = action
      const {loading } = payload
      return {
        ...state,
        loading
      };
    },

    setCandicateQuestions(state:AnswersOfQuestionsAnalysisStateType , action:any) {
      console.log(`setCandicateQuestions:`,action.payload)
      const {payload} = action
      const {questions } = payload
      return {
        ...state,
        loading:false,
        candidateQuestions:questions
      };
    },


  },
};

export default AnswersOfQuestionsAnalysisModel;