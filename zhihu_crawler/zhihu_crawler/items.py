# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class ZhihuCrawlerItem(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    pass

class ZhihuAnswerItem(scrapy.Item):
    question_id = scrapy.Field()
    answer_id = scrapy.Field()
    created_time = scrapy.Field()
    updated_time = scrapy.Field()
    voteup_count = scrapy.Field()
    comment_count = scrapy.Field()
    content = scrapy.Field()
    author_url_token = scrapy.Field()
    author_id = scrapy.Field()
    

class ZhihuUserItem(scrapy.Item):
    user_id = scrapy.Field()
    url_token = scrapy.Field()
    avatar_url = scrapy.Field()
    gender = scrapy.Field()
    headline = scrapy.Field()
    name = scrapy.Field()
    follower_count = scrapy.Field()
    type = scrapy.Field()
    #append
    description = scrapy.Field()
    location = scrapy.Field()
    company = scrapy.Field()
    job = scrapy.Field()
    business = scrapy.Field()
    school = scrapy.Field()
    major = scrapy.Field()


class ZhihuQuestionItem(scrapy.Item):
    question_id = scrapy.Field()
    title = scrapy.Field()
    created_time = scrapy.Field()
    update_time = scrapy.Field()
    #
    lastest_add_time = scrapy.Field() #上次添加项目时间，爬完更新
    lastest_total = scrapy.Field() #同


