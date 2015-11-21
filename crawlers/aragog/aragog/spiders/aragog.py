import scrapy
from scrapy import Spider
from scrapy.linkextractors import LinkExtractor
from scrapy.http.response.html import HtmlResponse

import ConfigParser,datetime
from ..utils.urlutils import get_netloc
from ..items.items import AragogItem
from difflib import SequenceMatcher
class Properties:
	def __init__(self):
		config = ConfigParser.RawConfigParser()
		config.read("aragog.properties")
		self.pagesToCrawl = int(config.get("settings","pagesToCrawl"))
		self.crawlDepth = config.get("settings","depthLimit")
		self.opDir = config.get("settings","opDir")
		self.allowedDomains = eval(config.get("settings","allowedDomains"))
		self.seeds = eval(config.get("settings","seeds"))
		self.name = config.get("settings","name")
		self.opEncoding = config.get("settings","opEncoding")
		self.allowPattern = config.get("settings","allowPattern")
		self.denyPattern = config.get("settings","denyPattern")
		
class aragogSpider(Spider):
	prop  = Properties()
	name = prop.name
	allowed_domains = prop.allowedDomains
	start_urls = prop.seeds
	allowPattern = prop.allowPattern
	denyPattern = prop.denyPattern
	#get_links = LinkExtractor(allow = (allowPattern) ,deny = (denyPattern), allow_domains=prop.allowedDomains).extract_links
	#get_links = LinkExtractor( allow_domains=(prop.allowedDomains[0])).extract_links
	#get_links = LinkExtractor(allow_domains=prop.allowedDomains).extract_links
	get_links = LinkExtractor(allow_domains=(get_netloc(prop.allowedDomains[0]))).extract_links
	docCount = 0
	def strSim(self,a,b):
		return SequenceMatcher(None, a,b ).ratio()
	def parse(self,response):
		item = AragogItem()
		item["crawledAt"] = datetime.datetime.utcnow()
		item["url"] = response.url
		item["status"] = response.status
		item["headers"] = response.headers
		item["body"] =  response.body
		yield item
		links = aragogSpider.get_links(response)
		links = sorted(links,key = lambda x : self.strSim(x.url,response.url) ,reverse=True)
		for link in links:
			print "[DEBUG] ", response.url , "\t",link.url , "\n"
			#print "[DEBUG LINK] ",link.url , "\n"
			#yield scrapy.Request(link.url, self.parse)


