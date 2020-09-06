# -*- coding: utf-8 -*-
import scrapy
import json
from zhihu_crawler.items import ZhihuUserItem

#this script is used to append new v2 users data
#it receive the old folder path, using user.txt,
#to generate users_v2.txt

class ZhihuUserSpider(scrapy.Spider):
    name = 'zhihu_user'
    allowed_domains = ['www.zhihu.com']
    
    url_pattern = "https://www.zhihu.com/api/v4/members/{}?include=locations,employments,gender,educations,business,voteup_count,thanked_Count,follower_count,following_count,cover_url,following_topic_count,following_question_count,following_favlists_count,following_columns_count,answer_count,articles_count,pins_count,question_count,commercial_question_count,favorite_count,favorited_count,logs_count,marked_answers_count,marked_answers_text,message_thread_token,account_status,is_active,is_force_renamed,is_bind_sina,sina_weibo_url,sina_weibo_name,show_sina_weibo,is_blocking,is_blocked,is_following,is_followed,mutual_followees_count,vote_to_count,vote_from_count,thank_to_count,thank_from_count,thanked_count,description,hosted_live_count,participated_live_count,allow_message,industry_category,org_name,org_homepage,badge[?(type=best_answerer)].topics"
    start_urls = []

    old_user_file_path = "/home/xuranus/Desktop/毕业设计/zhihu_crawler_v2/user.append"
    lines = open(old_user_file_path).read().split('\n')
    for line in lines:
        if(line!=''):
            url_token = json.loads(line)['url_token']
            start_urls.append(url_pattern.format(url_token))

    def parse(self, response):
        data = json.loads(response.text)

        user_id = data['id']
        url_token = data['url_token']
        name = data['name']
        avatar_url = data['avatar_url']
        gender = data['gender']
        headline = data['headline']
        follower_count = data['follower_count']
        user_type = data['type']
        #description
        description = data['description']
        #location
        location = ""
        try:
            locations = data['locations']
            if(len(locations)>0):
                location = locations[0]['name']
        except KeyError:
            print("KeyError")
        #education
        school = ""
        major = ""
        try:
            educations = data['educations']
            if(len(educations)>0):
                school = educations[0]['school']['name']
                major = educations[0]['major']['name']
        except KeyError:
            print("KeyError")
        #business
        business = ""
        try:
            business = data['business']['name']
        except KeyError:
            print("KeyError")
        #employments
        job = ""
        company = ""
        try:
            employments = data['employments']
            if(len(employments)>0):
                company = employments[0]['company']['name']
                job = employments[0]['job']['name']
        except KeyError:
            print("KeyError")
            
        #print(user_id,url_token,name,avatar_url,gender,headline,follower_count,user_type,description,location,school,major,job,company,business)

        userItem = ZhihuUserItem(
            user_id = user_id,
            url_token = url_token,
            avatar_url = avatar_url,
            gender = gender,
            headline = headline,
            name = name,
            follower_count = follower_count,
            type = user_type,
            #append
            description = description,
            location = location,
            company = company,
            job = job,
            business = business,
            school = school,
            major = major
        )
        
        yield userItem
