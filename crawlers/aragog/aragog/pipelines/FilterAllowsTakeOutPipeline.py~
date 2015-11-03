from ..items.items import AragogItem
from scrapy.exceptions import DropItem
from scrapy.selector import Selector

class FilterAllowsTakeOutPipeline(object):
	def isValidPage(self,item) :
		valid = False
		htmlTxt =item["body"]
		KEY = "Take-out"
		VALUE = "Yes"
		business = Selector(text=htmlTxt).xpath('//div[@class="biz-page-subheader"]')
		if len(business) > 0 :
			deets  = Selector(text=htmlTxt).xpath('//div[@class="column column-beta sidebar"]/div[@class="bordered-rail"]/div[@class="ywidget"]/ul[@class="ylist"]/li/div//dl')
			k = map(lambda x: x.strip() , deets.xpath(".//dt/text()").extract())
			v = map(lambda X : X.strip() , deets.xpath(".//dd/text()").extract())
			d = dict(zip(k, v))
			if d.has_key(KEY) and d[KEY]==VALUE :
				valid = True
		return valid
	def process_item(self, item, spider):
		if self.isValidPage(item) :
			return item
		else :
			raise DropItem("[DROP] Page not a Business Listing - Dropping Item")
