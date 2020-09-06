import { PageHeaderWrapper } from '@ant-design/pro-layout';
import React, { useEffect } from 'react';
import { Dispatch, AnyAction } from 'redux';
import { connect } from 'dva';
import { Button} from 'antd'

import {AnswersWithKeywordWithinTimeAnalysisStateType} from '@/models/analysisWithKeywordWithinTime'

import DisplayBoard from './DisplayBoard'
import SearchBlock from './SearchBlock'

export interface AnswersWithKeywordWithinTimeAnalysisProps {
  answersWithKeywordWithinTimeAnalysis: AnswersWithKeywordWithinTimeAnalysisStateType;
  dispatch: Dispatch<AnyAction>;
}


const AnswersWithKeywordWithinTimeAnalysis = (props: AnswersWithKeywordWithinTimeAnalysisProps) => {

  const { 
    loading,
    errorMsg,
    analysisResult, 
    timeCosts,
    text,
    begin,
    end
  } = props.answersWithKeywordWithinTimeAnalysis

  //TODO::remove latter
  useEffect(()=>{
    
    /*props.dispatch({
      type:'answersWithKeywordWithinTimeAnalysis/fetch',
      payload:{
        begin:'2020-03-20',
        end:'2020-04-05',
        keyword:'肺炎'
      }    
    })*/

  },[])

  if(analysisResult) {  //有结果，且正确
    return (
      <PageHeaderWrapper 
        content={
          <span>
            <span> <b>"{text},"</b> 
            从 <b>{begin}</b> 到 <b>{end}</b> 的分析结果，
            涉及关键字: <span>{analysisResult.keywords.map(keyword=>(<span key={keyword}><b>{keyword}</b> ,</span>))}</span>&nbsp;&nbsp;
            共计 <b>{analysisResult.answersNum}</b> 条回答，
            耗时<b>{timeCosts}</b>秒</span>
            <Button type="link" onClick={
              ()=>{
                props.dispatch({
                  type:'answersWithKeywordWithinTimeAnalysis/loadAnalysisState',
                  payload:{analysisResult:null,success:true}
                })
              }
            }>重新搜索</Button>
          </span>
        }>
            

          <DisplayBoard 
            begin={begin}
            end={end}
            analysisResult={analysisResult}
          />          
      </PageHeaderWrapper>
    )
  } else {
    return (
      <PageHeaderWrapper content={"按关键字搜索"}>
        <SearchBlock 
          loading={loading}
          errorMessage={errorMsg?errorMsg:''}
          handleSearch={(text:string,begin:string,end:string)=>{
            //console.log(text,begin,end)
            props.dispatch({
              type:'answersWithKeywordWithinTimeAnalysis/fetch',
              payload:{begin,end,text}
            })
          }}
        />
      </PageHeaderWrapper>
    )
  }
}


export default connect(
  ({answersWithKeywordWithinTimeAnalysis}:any)=>(
    {answersWithKeywordWithinTimeAnalysis}
))(AnswersWithKeywordWithinTimeAnalysis)
