import React, { useEffect } from 'react';
import { LocationDistributionType } from '@/models/analysis';
import { Scene, PolygonLayer, LineLayer, Popup } from '@antv/l7';
import { GaodeMap } from '@antv/l7-maps';

export interface ChoroplethMapProps {
  locations:LocationDistributionType
}

const ChoroplethMap: React.FC<ChoroplethMapProps>= props => {


  function locationNameToProvience(name:string):string {
    let allowedNames = ["北京市","天津市","河北省","山西省","内蒙古自治区","辽宁省","吉林省","黑龙江省","上海市","江苏省","浙江省","安徽省","福建省","江西省","山东省","河南省","湖北省","湖南省","广东省","广西壮族自治区","海南省","重庆市","四川省","贵州省","云南省","西藏自治区","陕西省","甘肃省","青海省","宁夏回族自治区","新疆维吾尔自治区","台湾省","香港特别行政区","澳门特别行政区"]
    if(allowedNames.findIndex(x=>x==name) >=0 ) 
      return name
    else 
      return ""
  }

  const locations = props.locations.filter(pair=>(locationNameToProvience(pair.name)!=""))


  useEffect(()=>{
    const scene = new Scene({
    id: 'map',
    map: new GaodeMap({
        pitch: 0,
        style: 'light',
        center: [ 107.042225, 37.66565 ],
        zoom: 3.87
      })
    });
    scene.on('loaded', () => {
      fetch(
        '/100000_full.json'
      )
      .then(res => res.json())
      .then(data => {
        data.features.forEach(element => {
          element.properties.density = 0
        });
        locations.forEach(loc=>{
          data.features.find(x=>x.properties.name==loc.name).properties.density = loc.count
        })
        console.log(data)
        return data
      })//
      .then(data => {
        const color = [ 'rgb(255,255,217)', 'rgb(237,248,177)', 'rgb(199,233,180)', 'rgb(127,205,187)', 'rgb(65,182,196)', 'rgb(29,145,192)', 'rgb(34,94,168)', 'rgb(12,44,132)' ];
        const layer = new PolygonLayer({})
          .source(data)
          .color(
            'density', d => {
              return d > 500 ? color[7] :
                d > 100 ? color[6] :
                  d > 40 ? color[5] :
                    d > 20 ? color[4] :
                      d > 10 ? color[3] :
                        d > 5 ? color[2] :
                          d > 0 ? color[1] :
                            color[0];
            }
          )
          .shape('fill')
          .active(true)
          .style({
            opacity: 1.0
          });
        const layer2 = new LineLayer({
          zIndex: 2
        })
          .source(data)
          .color('#fff')
          .active(true)
          .size(1)
          .style({
            lineType: 'dash',
            dashArray: [ 2, 2 ],
            opacity: 1
          });
        scene.addLayer(layer);
        scene.addLayer(layer2);
  
        layer.on('mousemove', e => {
          const popup = new Popup({
            offsets: [ 0, 0 ],
            closeButton: false
          })
            .setLnglat(e.lngLat)
            .setHTML(`<span>${e.feature.properties.name}: ${e.feature.properties.density}</span>`);
          scene.addPopup(popup);
        });
      });


    });

  },[])




  return(
    <div id="map" style={{'width':'100%','height':'100%'}}></div>
  )
}

export default ChoroplethMap;