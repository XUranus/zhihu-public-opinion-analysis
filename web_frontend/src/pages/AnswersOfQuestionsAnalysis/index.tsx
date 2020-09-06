import { PageHeaderWrapper } from '@ant-design/pro-layout';
import React from 'react';
import { Dispatch, AnyAction } from 'redux';
import { connect } from 'dva';
import  {AnswersOfQuestionsAnalysisStateType} from '@/models/analysisOfQuestion'
import SearchBlock from './SearchBlock'
import DisplayBoard from "./DisplayBoard";
import { Button } from 'antd';


export interface AnswersOfQuestionsAnalysisProps {
  answersOfQuestionsAnalysis: AnswersOfQuestionsAnalysisStateType;
  dispatch: Dispatch<AnyAction>;
}


const AnswersOfQuestionsAnalysis = (props: AnswersOfQuestionsAnalysisProps) => {

  const { analysisResult,timeCosts,loading,errorMsg,candidateQuestions} = props.answersOfQuestionsAnalysis
  //console.log(analysisResult,loading)


  if(!analysisResult) {
    return (
      <PageHeaderWrapper content="对具体问题下答案的分析结果" >
        <SearchBlock
          candidateQuestions={candidateQuestions}
          errorMessage={errorMsg}
          loading={loading}
          handleSearchCandidateQuestions={
            (text:string)=>{
              props.dispatch({
                type:'answersOfQuestionsAnalysis/loadCandidateQuestions',
                payload:{ text }
              })
            }
          }
          handleSubmitQuestionsIds={
            (questionIds:string)=>{
              props.dispatch({
                type:'answersOfQuestionsAnalysis/fetch',
                payload:{ questionIds }
              })
            }
          }
        />
      </PageHeaderWrapper>
    )
  } else return (
    <PageHeaderWrapper 
      content={<span>对以下 <b>{analysisResult.questions.length}</b> 个问题下答案的分析结果，
      耗时<b>{timeCosts}</b>秒
      <Button type="link" onClick={
        ()=>{
          props.dispatch({
            type:'answersOfQuestionsAnalysis/loadAnalysisState',
            payload:{analysisResult:null,candidateQuestions:null,success:true}
          })
        }
      }>重新搜索</Button>
      </span>
    }>
      <DisplayBoard analysisResult={analysisResult}/>
    </PageHeaderWrapper>
  );
};


export default connect(
  ({answersOfQuestionsAnalysis}:any)=>(
    {answersOfQuestionsAnalysis}
))(AnswersOfQuestionsAnalysis)
