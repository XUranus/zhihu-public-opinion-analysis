import React from 'react';
import { Avatar, List ,Card, Row, Col, Comment, Statistic, Button, Typography, Tabs, Badge} from 'antd'
import {UserOutlined} from '@ant-design/icons'
import {DonutChart, PieChart, BarChart, LineChart, RoseChart,OverlappedComboChart} from '@opd/g2plot-react'

import WordCloud from '@/components/WordCloud'
import {AnswersWithinTimeAnalysisResultType} from '@/models/analysisWithinTime'
import ChoroplethMap from '@/components/ChoroplethMap';
import { LikeFilled } from '@ant-design/icons';

export interface AnswersWithinTimeAnalysisProps {
  begin:string,
  end:string,
  analysisResult: AnswersWithinTimeAnalysisResultType;
}

//pure functional component


function getLocationSummary(locationDistribution:Array<any>) {
  var all = locationDistribution.filter(x=>x.name!="")
  const displayNum = 15
  var newData = all.slice(0,displayNum)
  var otherData = all.slice(displayNum,all.length)
  var otherSum = otherData.reduce((acc,cur)=>acc + cur.count, 0)
  if(otherSum>0) newData.push({name:"其他",count:otherSum})
  return newData
}

const DisplayBoard = (props: AnswersWithinTimeAnalysisProps) => {

  const {analysisResult, begin, end} = props

  const {
    answersNum,
    anonymousUsersNum,
    questionsNum,
    keywords,
    sentimentDistribution,
    hottestQuestions,
    mostVotedAnswers,
    highFrequencyAuthors,
    mostLikedAuthors,
    sentimentTrend,
    heatTrend,
    genderDistribution,
    mostFollowedUsers,
    locationDistribution,
    businessDistribution,
    companyDistribution,
    jobDistribution,
    schoolDistribution,
    usersPortrait
  } = analysisResult

  console.log({
    answersNum,
    anonymousUsersNum,
    questionsNum,
    keywords,
    sentimentDistribution,
    hottestQuestions,
    mostVotedAnswers,
    highFrequencyAuthors,
    mostLikedAuthors,
    sentimentTrend,
    heatTrend,
    genderDistribution,
    mostFollowedUsers,
    locationDistribution,
    businessDistribution,
    companyDistribution,
    jobDistribution,
    schoolDistribution,
    usersPortrait
  })

      
  return (
    <div>

      <Tabs defaultActiveKey="1" type="card" size="large">
        <Tabs.TabPane tab="关键词分析" key="1">
          <Row>
            <Col span={12}>  
              <Card style={{marginRight:5,marginLeft:5}} title={"词云"}>
                <WordCloud 
                  height={300}
                  words={keywords.map((pair,index) => ({
                    word: pair.word,
                    weight: pair.count,
                    id: index
                  }))}
                />
              </Card>
            </Col>

            <Col span={6}>
              <Card style={{marginRight:5,marginLeft:5}} title={"热门词汇"}> 
                <Row>
                  <Col span={12}>
                    <List dataSource={keywords.slice(0,5)} renderItem={(data)=>(<List.Item><span style={{fontSize:22}}>{data.word}&nbsp;<span style={{fontSize:10,color:'red'}}>{data.count}</span></span></List.Item>)}/>
                  </Col>
                  <Col span={12}>
                    <List dataSource={keywords.slice(5,10)} renderItem={(data)=>(<List.Item><span style={{fontSize:22}}>{data.word}&nbsp;<span style={{fontSize:10,color:'red'}}>{data.count}</span></span></List.Item>)}/>
                  </Col>
                </Row>
              </Card>
            </Col>

            <Col span={6}>
              <Card style={{marginRight:5,marginLeft:5,height:400}} title={"概要"}>
              <Row gutter={16}>
                  <Col span={12}><Statistic title="开始时间" value={begin} /></Col>
                  <Col span={12}><Statistic title="结束时间" value={end}/></Col>
                </Row><br/>
                <Row gutter={16}>
                  <Col span={12}><Statistic title="相关回答数" value={answersNum} /></Col>
                  <Col span={12}><Statistic title="相关问题数" value={questionsNum} /></Col>
                </Row><br/>
              </Card>
            </Col>

          </Row>
        </Tabs.TabPane>



        <Tabs.TabPane tab="热度分析" key="2">
          <Row>
            <Col span={24}>
              <Card style={{margin:5}} title="词条热度趋势(每小时)">
                <LineChart
                  animation={{appear:{animation:'clipingWithData'}}}
                  width={1540}
                  height={250}
                  description={{visible:true,text:`从${begin}到${end}`}}
                  forceFit={true}
                  data={heatTrend.hourTrend.map((x)=>({'小时':x.time, '热度':x.value}))}
                  smooth={true}
                  padding={'auto'}
                  xField={'小时'}
                  yField={'热度'}
                  xAxis={{visible: true, label: {visible: true, autoHide: true}}}
                  yAxis={{visible: true}}
                />
              </Card>
            </Col>
          </Row>

          <Row>
            <Col span={24}>
              <Card style={{margin:5}} title="词条热度趋势(每日)">
                <OverlappedComboChart
                  layers={[
                    {
                      type: 'area',
                      smooth:true,
                      name: '实际每日增长',
                      data: heatTrend.dayTrend,
                      xField: 'time',
                      yField: 'value',
                    },{
                      type: 'line',
                      name: '预测每日增长',
                      data: heatTrend.dayPredict,
                      xField: 'time',
                      yField: 'value',
                      color: '#f8ca45',
                      smooth: true
                    }]}
                />
              </Card>
            </Col>
          </Row>
        </Tabs.TabPane>



        <Tabs.TabPane tab="情感分析" key="3">
          <Row>
            <Col span={6}>
              <Card style={{margin:5}} title={"情感分布"}>
                <PieChart
                  height={250}
                  forceFit={true}
                  radius={0.8}
                  data={[
                    {type:'喜悦', value:sentimentDistribution.find(x=>x.type==0)?.count},
                    {type:'愤怒', value:sentimentDistribution.find(x=>x.type==1)?.count},
                    {type:'厌恶', value:sentimentDistribution.find(x=>x.type==2)?.count},
                    {type:'低落', value:sentimentDistribution.find(x=>x.type==3)?.count}
                  ]} 
                  angleField={'value'}
                  colorField={'type'}
                  label={{visible: true,type: 'inner'}}
                />
              </Card>
            </Col>

            <Col span={18}>
              <Card style={{margin:5}}>
                <LineChart
                  animation={{appear:{animation:'clipingWithData'}}}
                  width={1540}
                  height={300}
                  title={{visible: true, text: '词条负面指数趋势(每小时)'}}
                  description={{visible:true,text:`从${begin}到${end}`}}
                  forceFit={true}
                  data={sentimentTrend.hourTrend.map((x)=>({'小时':x.time, '热度':x.value}))}
                  smooth={true}
                  padding={'auto'}
                  xField={'小时'}
                  yField={'热度'}
                  xAxis={{visible: true, label: {visible: true, autoHide: true}}}
                  yAxis={{visible: true}}
                />
              </Card>
            </Col>
          </Row>


          <Row>
            <Col span={24}>
              <Card>
                <OverlappedComboChart
                  layers={[
                    {
                      type: 'column',
                      name: '实际每日',
                      data: sentimentTrend.dayTrend,
                      xField: 'time',
                      yField: 'value',
                    },{
                      type: 'line',
                      name: '预测每日',
                      data: sentimentTrend.dayPredict,
                      xField: 'time',
                      yField: 'value',
                      color: '#f8ca45',
                      smooth: true
                    }]}
                />
              </Card>
            </Col>
          </Row>
        </Tabs.TabPane>



        <Tabs.TabPane tab="地域分析" key="4">
          <Row>
            <Col span={15}>
              <Card style={{marginLeft:0}}>
                <div style={{height:400}}>
                  <ChoroplethMap locations={locationDistribution}/>
                </div>
              </Card>
            </Col>

            <Col span={9}>
              <Card style={{margin:5}}>
                <RoseChart
                  forceFit={true}
                  title={{visible: true,text: '主要地域分布'}}
                  radius={0.9}
                  data={getLocationSummary(locationDistribution)}
                  radiusField={'count'}
                  categoryField={'name'}
                  colorField={'name'}
                  label={{visible: true,type: 'outer',content: (text) => text.count}}
                />
              </Card>
            </Col>
          </Row>
        </Tabs.TabPane>


        <Tabs.TabPane tab="用户分析" key="5">
          <Row>
            <Col span={6}>
              <Card style={{margin:5}} title={"用户性别站比"}>
                <DonutChart
                  height={250}
                  forceFit={true}
                  radius={0.8}
                  padding={'auto'}
                  data={[
                    {'type':'男性', value:genderDistribution.find(x=>x.type==1)?.count},
                    {'type':'女性', value:genderDistribution.find(x=>x.type==-1)?.count},
                    //{'type':'未知', value:genderDistribution.find(x=>x.type==0)?.count},
                  ]}
                  angleField={'value'}
                  colorField={'type'}
                />
              </Card>
            </Col>

            <Col span={6}>
              <Card style={{margin:5}} title={"匿名比例"}>
                <PieChart
                  height={250}
                  forceFit={true}
                  radius={0.8}
                  data={[
                    {type:'匿名', value:anonymousUsersNum},
                    {type:'非匿名', value:answersNum},
                  ]} 
                  angleField={'value'}
                  colorField={'type'}
                  label={{visible: true,type: 'inner'}}
                />
              </Card>
            </Col>

            <Col span={12}>  
              <Card style={{margin:5}} title={"用户描述关键词"}>
                <WordCloud 
                  height={250}
                  words={usersPortrait.map((pair,index) => ({
                    word: pair.word,
                    weight: pair.count,
                    id: index
                  }))}
                />
              </Card>
            </Col>
          </Row>


          <Row>
            <Col span={6}>
              <Card style={{margin:5}} title={"用户大学分布"}>
                <BarChart
                  height={200}
                  forceFit={true}
                  data = {schoolDistribution.slice(0,5).map(x=>({"学校":x.name, "人数":x.count}))}
                  xField={'人数'}
                  yField={'学校'}
                  colorField={'学校'}
                  color={['#55A6F3', '#CED4DE', '#55A6F3', '#55A6F3', '#55A6F3']}
                  label={{visible: true,position: 'middle',adjustColor: false}}
                />
              </Card>
            </Col>

            <Col span={6}>
              <Card style={{margin:5}} title={"用户行业分布"}>
                <BarChart
                  forceFit={true}
                  height={200}
                  data = {businessDistribution.slice(0,5).map(x=>({"行业":x.name, "人数":x.count}))}
                  xField={'人数'}
                  yField={'行业'}
                  colorField={'行业'}
                  color={['#55A6F3', '#CED4DE', '#55A6F3', '#55A6F3', '#55A6F3']}
                  label={{visible: true,position: 'middle',adjustColor: false}}
                />
              </Card>
            </Col>

            <Col span={6}>
              <Card style={{margin:5}} title={"用户职位分布"}>
                <BarChart
                  forceFit={true}
                  height={200}
                  data = {jobDistribution.slice(0,5).map(x=>({"行业":x.name, "人数":x.count}))}
                  xField={'人数'}
                  yField={'行业'}
                  colorField={'行业'}
                  color={['#55A6F3', '#CED4DE', '#55A6F3', '#55A6F3', '#55A6F3']}
                  label={{visible: true,position: 'middle',adjustColor: false}}
                />
              </Card>
            </Col>

            <Col span={6}>
              <Card style={{margin:5}} title={"用户公司分布"}>
                <BarChart
                  forceFit={true}
                  height={200}
                  data = {companyDistribution.slice(0,5).map(x=>({"公司":x.name, "人数":x.count}))}
                  xField={'人数'}
                  yField={'公司'}
                  colorField={'公司'}
                  color={['#55A6F3', '#CED4DE', '#55A6F3', '#55A6F3', '#55A6F3']}
                  label={{visible: true,position: 'middle',adjustColor: false}}
                />
              </Card>
            </Col>

          </Row>
      

          <Row>
            <Col span={8}>
              <Card style={{margin:5}} title={"知名大V"}>
                <List
                  itemLayout="horizontal"
                  dataSource={mostFollowedUsers.slice(0,5)}
                  renderItem={item=>(
                    <List.Item>
                      <List.Item.Meta
                        avatar={<Avatar src={item.avatarUrl} />}
                        title={<a target="_blank" href={`https://www.zhihu.com/people/${item.urlToken}`}>{item.name}</a>}
                        description={item.headline}
                      />
                      <div>关注：{item.followerCount}</div>
                    </List.Item>
                  )}
                />
              </Card>
            </Col>

            <Col span={8}>
              <Card style={{margin:5}} title={"活跃用户"}>
                <List
                  itemLayout="horizontal"
                  dataSource={highFrequencyAuthors.slice(0,5)}
                  renderItem={item=>(
                    <List.Item>
                      <List.Item.Meta
                        avatar={<Avatar src={item.user.avatarUrl} />}
                        title={<a target="_blank" href={`https://www.zhihu.com/people/${item.user.urlToken}`}>{item.user.name}</a>}
                        description={item.user.headline}
                      />
                      <div>回答数：{item.times}</div>
                    </List.Item>
                  )}
                />
              </Card>
            </Col>

            <Col span={8}>
              <Card style={{margin:5}} title={"高赞作者"}>
                <List
                  itemLayout="horizontal"
                  dataSource={mostLikedAuthors.slice(0,5)}
                  renderItem={item=>(
                    <List.Item>
                      <List.Item.Meta
                        avatar={<Avatar src={item.user.avatarUrl} />}
                        title={<a target="_blank" href={`https://www.zhihu.com/people/${item.user.urlToken}`}>{item.user.name}</a>}
                        description={item.user.headline}
                      />
                      <div>收获赞：{item.count}</div>
                    </List.Item>
                  )}
                />
              </Card>
            </Col>
          </Row>

        </Tabs.TabPane>



        <Tabs.TabPane tab="热门问答" key="6">
          <Row >
            <Col span={24}>
              <Card >
                <List
                  style={{width:'900'}}
                  header={<Typography.Title level={4}>热门问题</Typography.Title>}
                  dataSource={hottestQuestions.slice(0,5)}
                  renderItem={item=>
                    <List.Item>
                      <List.Item.Meta
                        description={<Typography.Text strong>{item.question.title}</Typography.Text>}
                      />
                      <span>相关回答数:{item.answersNum}&nbsp;&nbsp;&nbsp;</span>
                      <a target="_blank" href={`https://www.zhihu.com/question/${item.question.id}`}>查看问题</a>
                    </List.Item>}
                />
              </Card>
            </Col>
          </Row>

          <Row>
            <Card>
            <List
              className="comment-list"
              header={<Typography.Title level={4}>高赞回答</Typography.Title>}
              dataSource={mostVotedAnswers}
              itemLayout="horizontal"
              renderItem={pair=>(
                <li>
                  <Typography.Text strong>{pair.question.title}</Typography.Text>
                  <Comment
                    actions={[
                      <span className="comment-basic-like">
                        <LikeFilled/>&nbsp;{pair.answer.voteUpCount}
                      </span>,
                      <span>
                        &nbsp;
                        <a target="_blank" href={`http://www.zhihu.com/question/${pair.answer.questionId}/answer/${pair.answer.id}`}>查看原文</a>
                      </span>
                    ]}
                    author={<a target="_blank" href={`https://www.zhihu.com/people/${pair.user.urlToken}`}>{pair.user.name}</a>}
                    avatar={<Avatar src={pair.user.avatarUrl} />}
                    content={pair.answer.content.slice(0,300)+'......'}
                    datetime={(()=>{
                      let date = new Date(pair.answer.createdTime*1000)
                      return `${date.getMonth()}-${date.getDate()}`
                    })()}
                  />
                </li>
              )}
            />
            </Card>
          </Row>
        </Tabs.TabPane>
      </Tabs>

    </div>
  );

};



export default DisplayBoard;
