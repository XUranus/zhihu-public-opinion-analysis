import React from 'react';
import { WordCloudChart } from '@opd/g2plot-react'

import {WordCloudPairDataType} from '@/models/wordCloud'


export interface WordCloudProps {
  width?: number ;
  height?: number;
  words: Array<WordCloudPairDataType>;
  shape?: string;
}

function wordCloudConfig(props:WordCloudProps) {
  let config = {
    width: props.width || 500,
    height: props.height || 400,
    data: props.words || [],
    //maskImage: 'https://gw.alipayobjects.com/mdn/rms_2274c3/afts/img/A*07tdTIOmvlYAAAAAAAAAAABkARQnAQ',
    wordStyle: {
      rotation: [-Math.PI / 2, Math.PI / 2],
      rotateRatio: 0.5,
      rotationSteps: 4,
      fontSize: [15, 80],
      color: (word:string, weight:number) => randomColor(),
      active: {
        shadowColor: '#333333',
        shadowBlur: 10,
      },
      gridSize: 10,
    },
    shape: props.shape || 'circle', //cardioid
    shuffle: false,
    backgroundColor: '#fff',
    tooltip: {
      visible: true,
    },
    selected: -1,
    onWordCloudHover: hoverAction,
  };
  console.log(config)
  return config
}

function randomColor() {
  const arr = [
    '#5B8FF9',
    '#5AD8A6',
    '#5D7092',
    '#F6BD16',
    '#E8684A',
    '#6DC8EC',
    '#9270CA',
    '#FF9D4D',
    '#269A99',
    '#FF99C3',
  ];
  return arr[Math.floor(Math.random() * (arr.length - 1))];
}

function hoverAction(item:WordCloudPairDataType, dimension:any, evt:any, start:any) {
  console.log('hover action', item && item.word);
}


const WordCloud: React.FC<WordCloudProps>= props => {
  const config = wordCloudConfig(props)
  return (
    <div style={{textAlign: 'center' }}>
        <WordCloudChart {...config}/>
    </div>
  );
}

export default WordCloud