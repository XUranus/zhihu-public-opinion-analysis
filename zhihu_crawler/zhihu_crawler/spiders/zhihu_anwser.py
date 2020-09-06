# -*- coding: utf-8 -*-
import scrapy
import json
from zhihu_crawler.items import ZhihuAnswerItem,ZhihuQuestionItem,ZhihuUserItem

class ZhihuAnswerSpider(scrapy.Spider):
    name = 'zhihu_answer'
    allowed_domains = ['zhihu.com']
    user_url_pattern = "https://www.zhihu.com/api/v4/members/{}?include=locations,employments,gender,educations,business,voteup_count,thanked_Count,follower_count,following_count,cover_url,following_topic_count,following_question_count,following_favlists_count,following_columns_count,answer_count,articles_count,pins_count,question_count,commercial_question_count,favorite_count,favorited_count,logs_count,marked_answers_count,marked_answers_text,message_thread_token,account_status,is_active,is_force_renamed,is_bind_sina,sina_weibo_url,sina_weibo_name,show_sina_weibo,is_blocking,is_blocked,is_following,is_followed,mutual_followees_count,vote_to_count,vote_from_count,thank_to_count,thank_from_count,thanked_count,description,hosted_live_count,participated_live_count,allow_message,industry_category,org_name,org_homepage,badge[?(type=best_answerer)].topics"
    
    #question_id = str(386404266)
    #start_urls = ['https://www.zhihu.com/api/v4/questions/'+ question_id +'/answers?include=data%5B*%5D.is_normal%2Cadmin_closed_comment%2Creward_info%2Cis_collapsed%2Cannotation_action%2Cannotation_detail%2Ccollapse_reason%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelevant_info%2Cquestion%2Cexcerpt%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cis_labeled%2Cis_recognized%2Cpaid_info%2Cpaid_info_content%3Bdata%5B*%5D.mark_infos%5B*%5D.url%3Bdata%5B*%5D.author.follower_count%2Cbadge%5B*%5D.topics&offset=&limit=20&sort_by=default&platform=desktop']

    start_urls_file_path = 'start_urls.txt' #file absolute path here 
    str = open(start_urls_file_path).read()
    start_urls = json.loads(str)
    
    def parse(self, response):
        response_json = json.loads(response.text)
        
        data = response_json['data']
        paging = response_json['paging']
        is_end = paging['is_end']
        is_start = paging['is_start']

        for index,answer in enumerate(data):
            #answer
            question_id = answer['question']['id']
            answer_id = answer['id']
            created_time = answer['created_time']
            updated_time = answer['updated_time']
            voteup_count = answer['voteup_count']
            comment_count = answer['comment_count']
            content = answer['content']
            #author
            author = answer['author']
            author_id = author['id']
            author_url_token = author['url_token']
            #author_avatar_url = author['avatar_url']
            #author_headline = author['headline']
            #author_gender = author['gender']
            #author_name = author['name']
            #author_follower_count = author['follower_count']
            #author_type = author['type']

            #question
            question = answer['question']
            question_title = question['title']
            question_update_time = question['updated_time']
            question_create_time = question['created']

            #避免爬到太老的答案，必须晚于 2020 3.20
            if(updated_time < 1584633600 or created_time < 1584633600):
                return

            answerItem = ZhihuAnswerItem(
                question_id = question_id,
                answer_id = answer_id,
                created_time = created_time,
                updated_time = updated_time,
                voteup_count = voteup_count,
                comment_count = comment_count,
                content = content,
                author_url_token = author_url_token,
                author_id = author_id,
            )
            
            questionItem = ZhihuQuestionItem(
                question_id = question_id,
                title = question_title,
                created_time = question_create_time,
                update_time = question_update_time,
                lastest_add_time = created_time,
                lastest_total = paging['totals']
            )
    
            #yield userItem
            yield scrapy.Request(self.user_url_pattern.format(author_url_token), callback = self.parse_user)

            yield answerItem

            if(is_start and index==1):#最新的一个回答
                yield questionItem
        
        
        if(is_end):
            return
        else:
            next_page = paging['next']
            yield scrapy.Request(next_page, callback = self.parse)
        pass




    def parse_user(self, response):
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