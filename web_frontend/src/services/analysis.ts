import request from '@/utils/request';
//this file is use to wrap all analysis request to backend server

export interface RequestAnswersWithKeywordWithinTimeAnalysisParamsType {
  begin: string,
  end: string,
  text: string
}

export interface RequestAnswersWithinTimeAnalysisParamsType {
  begin: string,
  end: string
}

export interface RequestAnswersOfQuestionsAnalysisParamsType {
  questionIds: number
}

export interface RequestSearchQuestionesByKeywordsParamsType {
  text: string
}

//embed params to uri
function get(url:string, params:object):string {
  url += '?';
  for(let k in params) {
    url += `${k}=${params[k]}&` ;
  }
  url = url.substring(0, url.length - 1);
  return url;
}

export async function requestAnswersWithKeywordWithinTimeAnalysis(payload: RequestAnswersWithKeywordWithinTimeAnalysisParamsType) {
  console.log('request AnswersWithKeyWordWithinTimeAnalysis',payload)
  return request(get('/mock/api/analysis/answersWithKeywordsWithinTimeAnalysis', payload),{
    method: 'GET'
  });
}

export async function requestAnswersWithinTimeAnalysis(payload: RequestAnswersWithinTimeAnalysisParamsType) {
  console.log('request AnswerquestAnswersWithinTimeAnalysisersWithKeyWordWithinTimeAnalysis',payload)
  return request(get('/mock/api/analysis/answersWithinTimeAnalysis', payload), {
    method: 'GET'
  });
}

export async function requestAnswersOfQuestionsAnalysis(payload: RequestAnswersOfQuestionsAnalysisParamsType) {
  console.log('request AnswersOfQuestionAnalysis',payload)
  return request(get('/mock/api/analysis/answersOfQuestionsAnalysis', payload), {
    method: 'GET'
  });
}

export async function requestSearchQuestionesByKeywords(payload: RequestSearchQuestionesByKeywordsParamsType) {
  console.log('request SearchQuestionByKeywords',payload)
  return request(get('/mock/api/search/searchQuestionByKeywords', payload), {
    method: 'GET'
  });
}


//realtime api
export async function requestRealtimeStatus() {
  console.log('request requestRealtimeStatus')
  return request(get('/mock/api/realtime/status', {}), {
    method: 'GET'
  });
} 