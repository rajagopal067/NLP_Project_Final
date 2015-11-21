# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy
class AragogItem(scrapy.Item):
	def toJSON(self):
		iJSON = {}
		iJSON["crawledAt"] = str(self["crawledAt"])
		iJSON["body"] = self["body"]
		iJSON["url"] = self["url"]
		iJSON["headers"] = self["headers"]
		iJSON["status"] = self["status"]
		return iJSON

	crawledAt = scrapy.Field()
	body = scrapy.Field()
	url = scrapy.Field()
	headers = scrapy.Field()
	status = scrapy.Field()  
   	pass
