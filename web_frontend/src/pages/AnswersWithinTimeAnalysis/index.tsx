import { PageHeaderWrapper } from '@ant-design/pro-layout';
import React from 'react';
import { Dispatch, AnyAction } from 'redux';
import { connect } from 'dva';
import {AnswersWithinTimeAnalysisStateType} from '@/models/analysisWithinTime'
import SearchBlock from './SearchBlock';
import DisplayBoard from './DisplayBoard';
import { Button } from 'antd';


export interface AnswersWithinTimeAnalysisProps {
  answersWithinTimeAnalysis: AnswersWithinTimeAnalysisStateType;
  dispatch: Dispatch<AnyAction>;
}


const AnswersWithinTimeAnalysis = (props: AnswersWithinTimeAnalysisProps) => {

  const { analysisResult,loading,errorMsg,timeCosts,begin,end } = props.answersWithinTimeAnalysis
  console.log(analysisResult,loading)


  if(!analysisResult) {
    return (
      <PageHeaderWrapper content="分析指定时间范围的全部答案" >
        <SearchBlock
          errorMessage={errorMsg||''}
          loading={loading}
          handleSearch={(begin:string,end:string)=>{
            props.dispatch({
              type:'answersWithinTimeAnalysis/fetch',
              payload:{begin,end}
            })
          }}
        />
      </PageHeaderWrapper>
    )
  } else return (
    <PageHeaderWrapper 
      content={<span>从 <b>{begin}</b> 到 <b>{end}</b> 的全部回答分析结果，
        共计 <b>{analysisResult.answersNum}</b> 条回答， 
        耗时 <b>{timeCosts}</b> 秒
        <Button type="link" onClick={
          ()=>{
            props.dispatch({
              type:'answersWithinTimeAnalysis/loadAnalysisState',
              payload:{analysisResult:null,success:true}
            })
          }
        }>重新搜索</Button>
        </span>}>     
        <DisplayBoard
          analysisResult={analysisResult}
          begin={begin}
          end={end}
        />
    </PageHeaderWrapper>
  );
};


export default connect(
  ({answersWithinTimeAnalysis}:any)=>(
    {answersWithinTimeAnalysis}
))(AnswersWithinTimeAnalysis)
