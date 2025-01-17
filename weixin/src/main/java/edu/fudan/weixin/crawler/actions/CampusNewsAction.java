package edu.fudan.weixin.crawler.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import edu.fudan.eservice.common.utils.CommonUtil;
import edu.fudan.eservice.common.utils.EncodeHelper;
import edu.fudan.eservice.common.utils.MongoUtil;
import edu.fudan.weixin.entity.News;

@ParentPackage("servicebase")
@Namespace("/crawler")
public class CampusNewsAction extends CrawlerBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7716341737255032152L;

	protected String listid;
	
	protected static final String RD="http://news.fudan.edu.cn";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Action(value = "newslist", results = { @Result(type = "json", params = {
			"root", "list" }) })
	public String list() {
		Cache c = CacheManager.getInstance().getCache("News");
		String ckey = "newslist"+listid + page;
		Element ele = c.get(ckey);
		if (!CommonUtil.isEmpty(ele)) {
			list = (List) ele.getObjectValue();

		} else {
			StringBuffer retstr = fetch(RD+"/news/"+listid+"/"+page+".html");
			Parser p = Parser.createParser(retstr.toString(), "utf-8");
			list = new ArrayList<News>();
			try {
				NodeList ls = p
						.extractAllNodesThatMatch(new HasAttributeFilter("class","date"));
				SimpleNodeIterator i = ls.elements();
				while (i.hasMoreNodes()) {
					Node n = i.nextNode();
					if (n instanceof TagNode) {
						TagNode tn = (TagNode) n;
						News news = new News();
						news.setPubdate(tn.toPlainTextString());
						Node tmp=tn.getNextSibling();
						while(tmp!=null &&!(tmp instanceof LinkTag))
							tmp=tmp.getNextSibling();
						if(tmp!=null)
						{
							LinkTag link=(LinkTag)tmp;
							news.setId(link.getAttribute("href"));
							news.setTitle(link.getAttribute("title"));
						}
						list.add(news);
					}
				}
				c.put(new Element(ckey, list));
			} catch (ParserException e) {

				e.printStackTrace();
			}
		}

		return SUCCESS;
	}

	@Action(value = "newscontent", results = { @Result(type = "json", params = {
			"root", "en" }) })
	public String content() {
		Cache c = CacheManager.getInstance().getCache("News");
		String ckey = "newscontent" + newsid;
		Element ele = c.get(ckey);
		if (!CommonUtil.isEmpty(ele)) {
			en = (News) ele.getObjectValue();
		} else {
			StringBuffer retstr = fetch(RD+"/"
					+ newsid);
			Parser p = Parser.createParser(retstr.toString(), "utf-8");
			try {
				NodeList nl = p.extractAllNodesThatMatch(new OrFilter(new NodeFilter[]{
						new TagNameFilter("h1"),new HasAttributeFilter("id",
								"endtext")}));
				SimpleNodeIterator i = nl.elements();
				en = new News();
				en.setId(newsid);
				while (i.hasMoreNodes()) {
					Node n = i.nextNode();
					if (n instanceof TagNode) {
						TagNode tn = (TagNode) n;
						if (tn.getTagName().equalsIgnoreCase("h1"))
							en.setTitle(tn.toPlainTextString());
						
						if (tn.getTagName().equalsIgnoreCase("div")) {
							NodeList ls = new NodeList();
							tn.collectInto(ls, new NodeClassFilter(
									ImageTag.class));
							SimpleNodeIterator j = ls.elements();
							DBCollection col = MongoUtil.getInstance().getDB()
									.getCollection("CrawlerImages");
							while (j.hasMoreNodes()) {
								ImageTag it = (ImageTag) j.nextNode();
								it.removeAttribute("width");
								it.removeAttribute("height");
								it.removeAttribute("style");
								it.setAttribute("class", "img-responsive");
								String imgid = EncodeHelper.digest(
										it.getImageURL(), "MD5");
								BasicDBObject obj = new BasicDBObject("id",
										imgid);
								DBObject dbo = col.findOne(obj);
								if (dbo == null)
									col.save(obj.append("url", it.getImageURL()));
								it.setImageURL("crawler/image.act?id=" + imgid);
							}
							ls=new NodeList();
							tn.collectInto(ls, new NodeClassFilter(LinkTag.class));
							 j = ls.elements();
							while (j.hasMoreNodes()) {
								LinkTag link=(LinkTag)j.nextNode();
								String linkstr=link.extractLink();
								if(link.isHTTPLikeLink()&&!linkstr.startsWith("http")){
									if(linkstr.startsWith("/"))
										link.setLink(RD+link.extractLink());
									else
									{
										int tk=newsid.lastIndexOf("/");
										if(tk>0)
										{
											link.setLink(RD+newsid.substring(0,tk+1)+linkstr);
										}else
										{
											link.setLink(RD+linkstr);
										}
									}
								}
								}
							en.setContent(tn.toHtml());
						}

					}
				}
			} catch (ParserException e) {

				e.printStackTrace();
			}
			if (!CommonUtil.isEmpty(en) && !CommonUtil.isEmpty(en.getContent()))
				c.put(new Element(ckey, en));
		}
		return SUCCESS;
	}

	public String getListid() {
		return listid;
	}

	public void setListid(String listid) {
		this.listid = listid;
	}

}
