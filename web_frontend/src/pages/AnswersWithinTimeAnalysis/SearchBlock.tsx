import React, { useState } from 'react';
import { DatePicker, Card, Spin, Button} from 'antd'
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

  const doSearch=()=>{
      handleSearch(range[0].format('YYYY-MM-DD'),range[1].format('YYYY-MM-DD'))
  }

  return(
    <Card style={{textAlign:'center'}}>
      <Spin size="large" spinning={loading}>
        <img style={{width:200,height:130}} src={"zhihu_logo.jpg"} />
        <div style={{margin:'auto',maxWidth:500,paddingBottom:50}}>
          <DatePicker.RangePicker 
            defaultValue={[sevenDaysBefore,today]} 
            onChange={value => setRange([value[0],value[1]])}
          />  
          <br/><br/>
          <Button 
            size="large" 
            type="primary"
            onClick={doSearch}  
          >
              分析全部
          </Button>
          <span style={{color:'red'}}>{errorMessage}</span>   
        </div>
      </Spin>
    </Card>
  )
}


export default SearchBlock