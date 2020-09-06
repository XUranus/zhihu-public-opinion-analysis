import React, { useEffect, useState } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Spin, List, Row, Col, Statistic, Typography, message } from 'antd';
import {connect} from 'dva';
import {RealtimeStatusStateType,RealtimeStatusType} from '@/models/realtimeStatus'
import WordCloud from '@/components/WordCloud';
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons';
import Websocket from 'react-websocket';

const wsUrl = "ws://192.168.0.108:8899/api/realtime/ws"

export interface RealtimeStatusProps {
  realtimeStatus:RealtimeStatusStateType
}

export interface DisplayProps {
  status:RealtimeStatusType
}


const DataStatisticCard=()=>{

  const [statistic, setStatistic] = useState([0,0,0,0])
  const [todayQuestionsNum,todayAnswersNum,totalQuestionsNum,totalAnswersNum] = statistic

  const handleWsMessage=(msg:string)=>{
    console.log(`ws message [${msg}]`)
    const newStatistic = msg.split(" ").map(x=>Number.parseInt(x))
    setStatistic(newStatistic)
  }

  let yesterdayQuestionsNum = totalQuestionsNum - todayQuestionsNum
  let yesterdayAnswersNum = totalAnswersNum - todayAnswersNum
  if(yesterdayAnswersNum==0) yesterdayAnswersNum = 1
  if(yesterdayQuestionsNum==0) yesterdayQuestionsNum = 1
  const questionsNumUpRate = ((todayQuestionsNum - yesterdayQuestionsNum) / yesterdayQuestionsNum)
  const answersNumUpRate = ((todayAnswersNum - yesterdayAnswersNum) / yesterdayAnswersNum )

  return(
    <Card style={{margin:5}}>

      <Websocket onMessage={handleWsMessage} url={wsUrl}/>

      <Typography.Title level={4}>数据量</Typography.Title><br/>
      <Row gutter={16}>
        <Col span={12}><Statistic title="总问题" value={totalQuestionsNum}/></Col>
        <Col span={12}><Statistic title="总回答" value={totalAnswersNum}/></Col>
      </Row>
      <br/>
      <Row gutter={16}>
        <Col span={12}><Statistic title="新增问题" value={todayQuestionsNum}/></Col>
        <Col span={12}><Statistic title="新增回答" value={todayAnswersNum}/></Col>
      </Row>    
      <br/>
      <Row gutter={16}>
        <Col span={12}>
          <Card size="small">
            <Statistic
              title="较昨日"
              value={questionsNumUpRate}
              suffix={"%"}
              valueStyle={{color:'#cf1322'}}
              precision={2}
              prefix={questionsNumUpRate > 0?<ArrowUpOutlined/>:<ArrowDownOutlined/>}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card size="small">
            <Statistic
              title="较昨日"
              value={answersNumUpRate}
              suffix={"%"}
              valueStyle={{color:'#cf1322'}}
              precision={2}
              prefix={answersNumUpRate > 0?<ArrowUpOutlined/>:<ArrowDownOutlined/>}
            />
          </Card>
        </Col>
      </Row>        
    </Card>
  )
}

const Display = (props:DisplayProps)=>{
  const {status} = props
  const {
    hotWords,
    hotQuestions,
    negativeQuestions
  } = status

  return(
    <div>
    
      <Row>

        <Col span={15}>
          <Card style={{margin:5}}>
            <WordCloud 
                height={330}
                width={800}
                words={hotWords.map((pair,index) => ({
                  word: pair.word,
                  weight: pair.count,
                  id: index
                }))}
              />
          </Card>
        </Col>

        <Col span={3}>
          <Card style={{margin:5}}>
            <List 
              size="small"
              dataSource={hotWords.slice(0,7)}
              header={<Typography.Title level={4}>热门词汇</Typography.Title>}
              renderItem={item=><List.Item>
                {item.word}&nbsp;{item.count}
              </List.Item>}
              >

            </List>
          </Card>
        </Col>

        <Col span={6}>
          <DataStatisticCard/>
        </Col>

      </Row>


      <Row >
        <Col span={12}>
          <Card style={{margin:5}}>
            <List
              style={{width:'900'}}
              header={<Typography.Title level={4}>热门问题</Typography.Title>}
              dataSource={hotQuestions}
              renderItem={item=>
                <List.Item>
                  <List.Item.Meta
                    description={<Typography.Text strong>{item.question.title}</Typography.Text>}
                  />
                  <span>回答:{item.answersNum}&nbsp;&nbsp;&nbsp;</span>
                  <a target="_blank" href={`https://www.zhihu.com/question/${item.question.id}`}>查看问题</a>
                </List.Item>}
            />
          </Card>
        </Col>

        <Col span={12}>
          <Card style={{margin:5}}>
            <List
              style={{width:'900'}}
              header={<Typography.Title level={4}>负面舆情</Typography.Title>}
              dataSource={negativeQuestions}
              renderItem={item=>
                <List.Item>
                  <List.Item.Meta
                    description={<Typography.Text strong>{item.question.title}</Typography.Text>}
                  />
                  <span>负面言论比例:{item.rate.toFixed(2)}&nbsp;&nbsp;&nbsp;</span>
                  <a target="_blank" href={`https://www.zhihu.com/question/${item.question.id}`}>查看问题</a>
                </List.Item>}
            />
          </Card>
        </Col>
      </Row>

    </div>
  )
}

const TimeBar = ()=>{
  const [time,setTime] = useState(new Date().toLocaleDateString() +' '+ new Date().toLocaleTimeString())
  useEffect(()=>{
    setInterval(()=>{
      setTime(new Date().toLocaleDateString() +' '+ new Date().toLocaleTimeString())
    },1000)
  },[])
  return (<div>{time}</div>)
}

const RealtimeStatus = (props:RealtimeStatusProps)=>{

  const {realtimeStatus} = props
  const {loading,status} = realtimeStatus

  useEffect(()=>{
    props.dispatch({type:'realtimeStatus/fetch',payload:{}})

    setInterval(()=>{
      props.dispatch({type:'realtimeStatus/fetch',payload:{}})
    },1000*60*10)
  },[])

  return (
    <PageHeaderWrapper title={<TimeBar/>}>
    <Spin size="large" spinning={loading}></Spin>
    {status?<Display status={status}/>:null}
    </PageHeaderWrapper>
  )
}


export default connect(
  ({realtimeStatus}:any)=>(
    {realtimeStatus}
))(RealtimeStatus)
