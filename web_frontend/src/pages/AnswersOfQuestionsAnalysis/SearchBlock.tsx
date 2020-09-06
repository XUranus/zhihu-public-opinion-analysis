import React, { useState } from 'react';
import {Card, Spin, Table, Button} from 'antd'
import Search from 'antd/lib/input/Search';
import { ZhihuQuestionType } from '@/models/analysis';
import moment from 'moment';

export interface SearchBlockProps {
  handleSearchCandidateQuestions:Function,
  handleSubmitQuestionsIds:Function,
  errorMessage?:string,
  candidateQuestions?:Array<ZhihuQuestionType>
  loading:boolean
} 

const SearchBlock = (props: SearchBlockProps) => {

  const {handleSearchCandidateQuestions, errorMessage, loading,candidateQuestions,handleSubmitQuestionsIds} = props 
  const [warningMessage,setWarning] = useState('')
  const [selectedRowkeys,setSelectedRowkeys] = useState([])

  const doSearchCandidateQuestions=(value:any)=>{
    //console.log(value,range)
    if(value!="")
    handleSearchCandidateQuestions(value)
    else 
      setWarning('关键字不能为空！')
  }

  const doSubmitQuestionsIds=()=>{
    let questionIds = ""
    selectedRowkeys.forEach(id=>questionIds+=`${id}_`)
    questionIds = questionIds.substring(0,questionIds.length-1)
    handleSubmitQuestionsIds(questionIds)
  }

  return(
    <Card style={{textAlign:'center'}}>
      <Spin size="large" spinning={loading}>
        <img style={{width:200,height:130}} src={"zhihu_logo.jpg"} />
        <div style={{margin:'auto',maxWidth:500,paddingBottom:50}}>
          <Search 
            loading={loading}
            size="large"
            placeholder="输入问题关键字或问题ID" 
            enterButton 
            onSearch={value => doSearchCandidateQuestions(value)}
            onChange={value => setWarning('')}
          />

          <br/><span style={{color:'red'}}>{warningMessage}</span> <br/>        
        </div>

        {candidateQuestions?
        <div>
          <Table 
            size="middle"
            pagination={{defaultPageSize:5}}
            dataSource={candidateQuestions.map(x=>({...x,key:x.id}))}
            columns={[
              {title:"问题", dataIndex:"title"},
              {title:"ID", dataIndex:"id"},
              {title:"创建时间", dataIndex:"createdTime",render:(timestamp)=>(moment(timestamp).format('YYYY-MM-DD'))}
            ]}
            rowSelection={{
              selectedRowkeys,
              onChange:setSelectedRowkeys
            }}
            />
          <Button 
            hidden={candidateQuestions.length==0}
            type="primary"
            disabled={selectedRowkeys.length==0}
            size="large"
            onClick={doSubmitQuestionsIds}  
          >
            分析{selectedRowkeys.length}个问题
          </Button>
        </div>:null}
        <span style={{color:'red'}}>{errorMessage}</span>

      </Spin>
    </Card>
  )
}


export default SearchBlock