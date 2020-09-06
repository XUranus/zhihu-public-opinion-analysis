import React, { useState } from 'react';
import { DatePicker, Card, Spin} from 'antd'
import Search from 'antd/lib/input/Search';
import moment from 'moment';

export interface SearchBlockProps {
  handleSearch:Function,
  errorMessage:string,
  loading:boolean
} 

const SearchBlock = (props: SearchBlockProps) => {

  const {handleSearch, errorMessage, loading} = props 

  const today = moment().startOf('day')
  const sevenDaysBefore = moment().startOf('day').subtract(7,'days')
  const [range,setRange] = useState([sevenDaysBefore, today])
  const [warningMessage,setWarning] = useState('')

  const doSearch=(value:any)=>{
    //console.log(value,range)
    if(value!="")
      handleSearch(value,range[0].format('YYYY-MM-DD'),range[1].format('YYYY-MM-DD'))
    else 
      setWarning('关键字不能为空！')
  }

  return(
    <Card style={{textAlign:'center'}}>
      <Spin size="large" spinning={loading}>
        <img style={{width:200,height:130}} src={"zhihu_logo.jpg"} />
        <div style={{margin:'auto',maxWidth:500,paddingBottom:50}}>
          <Search 
            loading={loading}
            size="large"
            placeholder="输入关键字" 
            enterButton 
            onSearch={value => doSearch(value)}
            onChange={value => setWarning('')}
          />
          <br/><br/>
          <DatePicker.RangePicker 
            defaultValue={[sevenDaysBefore,today]} 
            onChange={value => setRange([value[0],value[1]])}
          />  
          <br/><br/>
          <span style={{color:'red'}}>{errorMessage}</span>
          <span style={{color:'red'}}>{warningMessage}</span>     
        </div>
      </Spin>
    </Card>
  )
}


export default SearchBlock