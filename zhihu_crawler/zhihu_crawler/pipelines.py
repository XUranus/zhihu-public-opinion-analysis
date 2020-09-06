# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html

from scrapy.exporters import JsonLinesItemExporter
from zhihu_crawler.items import ZhihuAnswerItem,ZhihuUserItem,ZhihuQuestionItem

class ZhihuCrawlerPipeline(object):
    def __init__(self):
        self.fp1 = open('./data/answers.txt','wb')
        self.fp2 = open('./data/users_v2.txt','wb')
        self.fp3 = open('./data/questions.txt','wb')
        self.exporter1 = JsonLinesItemExporter(self.fp1, ensure_ascii = False, encoding = 'utf-8')
        self.exporter2 = JsonLinesItemExporter(self.fp2, ensure_ascii = False, encoding = 'utf-8')
        self.exporter3 = JsonLinesItemExporter(self.fp3, ensure_ascii = False, encoding = 'utf-8')

    def open_spider(self,spider):
        print('spider has opened.')

    def process_item(self, item, spider):
        if(isinstance(item,ZhihuAnswerItem)):
            self.exporter1.export_item(item)
        elif(isinstance(item,ZhihuUserItem)):
            self.exporter2.export_item(item)
        elif(isinstance(item,ZhihuQuestionItem)):
            self.exporter3.export_item(item)
        return item

    def close_spider(self,spider):
        self.exporter1.finish_exporting()
        self.exporter2.finish_exporting()
        self.exporter3.finish_exporting()
        self.fp1.close()
        self.fp2.close()
        self.fp3.close()
        print('spider has closed.')
