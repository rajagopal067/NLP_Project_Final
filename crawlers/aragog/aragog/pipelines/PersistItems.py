from ..items.items import AragogItem
from ..spiders.aragog import Properties
from scrapy.exceptions import DropItem
import os
import json

class PersistItemsPipeline(object):
	docCount = 0
	uniq = []
	def __init__(self, crawler):
        	self.crawler = crawler
	@classmethod
    	def from_crawler(cls, crawler):
        	return cls(crawler=crawler)

	def process_item(self, item, spider):
		prop = Properties()
		#if item["url"].split('?')[0] in PersistItemsPipeline.uniq :
		#	raise DropItem("[DROP] Page already Persisted. Dropping : " + item["url"])
		PersistItemsPipeline.uniq.append(item["url"].split('?')[0])	
		PersistItemsPipeline.docCount = PersistItemsPipeline.docCount+1
		if PersistItemsPipeline.docCount > prop.pagesToCrawl : 
			self.crawler.engine.close_spider(spider, 'Target Reached, Shutting down ARAGOG')
		if not os.path.exists(prop.opDir):
   			os.makedirs(prop.opDir)
		if prop.opEncoding == "JSON":
			fileName = prop.opDir+ "/" + str(PersistItemsPipeline.docCount) + '.JSON'
			jsonStr = json.dumps(item.toJSON(),indent=4)
			with open(fileName,'w+') as f:
				f.write(jsonStr)
				f.close()
		else:
			fileName = prop.opDir+ "/" + str(PersistItemsPipeline.docCount) + '.HTML'
			with open(fileName,'w+') as f:
				f.write(item["body"])
				f.close()
		print "PERSISTING-- ",item["url"]
		return item

