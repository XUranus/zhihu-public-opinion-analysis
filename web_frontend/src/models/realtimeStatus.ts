import { Effect } from 'dva';
import { Reducer } from 'redux';

import {
  ZhihuQuestionType,
  KeywordsType,
  HottestQuestionsType,
} from './analysis'

import {requestRealtimeStatus} from '@/services/analysis'

export interface NegativeQuestionsItemType {
  question:ZhihuQuestionType,
  rate:number
}

export type NegativeQuestionsType = Array<NegativeQuestionsItemType>

export interface RealtimeStatusType {
  hotWords:KeywordsType,
  hotQuestions:HottestQuestionsType,
  negativeQuestions:NegativeQuestionsType
}

export interface RealtimeStatusStateType {
  loading: boolean;
  errorMsg?: string;
  status?:RealtimeStatusType
}

//TODO::this should be a pure component
export interface RealtimeStatusModelType {
  namespace: string
  state: RealtimeStatusStateType;
  effects: {
    fetch: Effect
  };
  reducers: {
    loadState: Reducer<RealtimeStatusStateType>,
    loadNum: Reducer<RealtimeStatusStateType>
  };
}

const RealtimeStatusModel: RealtimeStatusModelType = {
  namespace: 'realtimeStatus',
  state: {
    loading: true
  },

  effects: {
    *fetch({payload}, { call, put }) { //载入分析数据
      const response = yield call(requestRealtimeStatus, payload);
      //console.log(`fetch: `,payload)
      yield put({
        type: 'loadState',
        payload: response,
      });
    },
  },

  reducers: {
    loadState(state:RealtimeStatusStateType , action:any) {
      console.log(`loadState:`,action.payload)
      const {payload} = action //console.log(payload)
      const {success,message } = payload
      if(success) {
        return {
          ...state,
          loading: false,
          errorMsg:null,
          ...payload
        };
      } else { //false
        return {
          ...state,
          loading: true,
          errorMsg:message
        };
      }
    },

    loadNum(state:RealtimeStatusStateType , action:any) {
      console.log(`loadNum:`,action.payload)
      const {payload} = action //console.log(payload)
      const {status} = state
      const  newStatus = {...status,payload}
      return {
        ...state,
        loading: false,
        status:newStatus
      };
      
    },


  },
};

export default RealtimeStatusModel;