# -*- coding: utf-8 -*-
import scrapy
import json
from zhihu_crawler.items import ZhihuAnswerItem,ZhihuQuestionItem,ZhihuUserItem

class ZhihuCrawlTimeEvaluateSpider(scrapy.Spider):
    name = 'zhihu_crawl_time_evaluate'
    allowed_domains = ['zhihu.com']

    start_urls = []

    sumAnswer = 0

    def parse(self, response):
        response_json = json.loads(response.text)
        
        data = response_json['data']
        paging = response_json['paging']

        answer = data[0]
        question = answer['question']
        question_title = question['title']
        totals = paging['totals']

        #question
        question_title = question['title']
        question_update_time = question['updated_time']
        question_create_time = question['created']
        question_id = question['id']

        questionItem = ZhihuQuestionItem(
            question_id = question_id,
            title = question_title,
            created_time = question_create_time,
            update_time = question_update_time,
        )

        #yield questionItem

        print(question_title)
        print(totals)
        self.sumAnswer = self.sumAnswer + totals
        print('total %s' %self.sumAnswer)
        

        pass