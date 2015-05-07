package io.bloc.android.blocly.api.network;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;

/**
 * Created by tonyk_000 on 5/1/2015.
 */
public class GetFeedsNetworkRequest extends NetworkRequest<List<GetFeedsNetworkRequest.FeedResponse>>{

    public static final int ERROR_PARSING = 3;

    private static final String XML_TAG_TITLE = "title";
    private static final String XML_TAG_DESCRIPTION = "description";
    private static final String XML_TAG_LINK = "link";
    private static final String XML_TAG_ITEM = "item";
    private static final String XML_TAG_PUB_DATE = "pubDate";
    private static final String XML_TAG_GUID = "guid";
    private static final String XML_TAG_ENCLOSURE = "enclosure";
    private static final String XML_ATTRIBUTE_URL = "url";
    private static final String XML_ATTRIBUTE_TYPE = "type";

    // #7
    DataSource [] dataSource;

    public GetFeedsNetworkRequest(DataSource... dataSource) {
        this.dataSource = dataSource;
    }

    // #8
    @Override
     public List<RssFeed> performRequest()  {
        List<RssFeed> responseFeeds = new ArrayList<RssFeed>(dataSource.length);
        for (DataSource feedData : dataSource) {
            InputStream inputStream = openStream(feedData);
            if (inputStream == null) {
                return null;
            }
            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
// #5
                Document xmlDocument = documentBuilder.parse(inputStream);

                String channelTitle = optFirstTagFromDocument(xmlDocument, XML_TAG_TITLE);
                String channelDescription = optFirstTagFromDocument(xmlDocument, XML_TAG_DESCRIPTION);
                String channelURL = optFirstTagFromDocument(xmlDocument, XML_TAG_LINK);

                // #6
                NodeList allItemNodes = xmlDocument.getElementsByTagName(XML_TAG_ITEM);
                List<RssItem> responseItems = new ArrayList<RssItem>(allItemNodes.getLength());
                for (int itemIndex = 0; itemIndex < allItemNodes.getLength(); itemIndex++) {
// #7
                    String itemURL = null;
                    String itemTitle = null;
                    String itemDescription = null;
                    String itemGUID = null;
                    String itemPubDate = null;
                    String itemEnclosureURL = null;
                    String itemEnclosureMIMEType = null;

                    //8
                    Node itemNode = allItemNodes.item(itemIndex);
                    NodeList tagNodes = itemNode.getChildNodes();
                    for (int tagIndex = 0; tagIndex < tagNodes.getLength(); tagIndex++) {
                        Node tagNode = tagNodes.item(tagIndex);
                        String tag = tagNode.getNodeName();
// #9
                        if (XML_TAG_LINK.equalsIgnoreCase(tag)) {
                            itemURL = tagNode.getTextContent();
                        } else if (XML_TAG_TITLE.equalsIgnoreCase(tag)) {
                            itemTitle = tagNode.getTextContent();
                        } else if (XML_TAG_DESCRIPTION.equalsIgnoreCase(tag)) {
                            itemDescription = tagNode.getTextContent();
                        } else if (XML_TAG_ENCLOSURE.equalsIgnoreCase(tag)) {
// #10
                            NamedNodeMap enclosureAttributes = tagNode.getAttributes();
                            itemEnclosureURL = enclosureAttributes.getNamedItem(XML_ATTRIBUTE_URL).getTextContent();
                            itemEnclosureMIMEType = enclosureAttributes.getNamedItem(XML_ATTRIBUTE_TYPE).getTextContent();
                        } else if (XML_TAG_PUB_DATE.equalsIgnoreCase(tag)) {
                            itemPubDate = tagNode.getTextContent();
                        } else if (XML_TAG_GUID.equalsIgnoreCase(tag)) {
                            itemGUID = tagNode.getTextContent();
                        }
                    }
                    RssItem rssItem = new RssItem(itemGUID,itemTitle,itemDescription, itemURL,"",0,0,false,false,false);
                    responseItems.add(rssItem);
                }
                RssFeed rssFeed = new RssFeed(channelTitle,channelDescription,channelURL,"");
                responseFeeds.add(rssFeed);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                setErrorCode(ERROR_IO);
                return null;
            } catch (SAXException e) {
                e.printStackTrace();
                setErrorCode(ERROR_PARSING);
                return null;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                setErrorCode(ERROR_PARSING);
                return null;
            }
        }
        return responseFeeds;
    }
    private String optFirstTagFromDocument(Document document, String tagName) {
        NodeList elementsByTagName = document.getElementsByTagName(tagName);
        if (elementsByTagName.getLength() > 0) {
            return elementsByTagName.item(0).getTextContent();
        }
        return null;
    }
    public static class FeedResponse {
        public final String channelFeedURL;
        public final String channelTitle;
        public final String channelURL;
        public final String channelDescription;
        public final List<ItemResponse> channelItems;

        FeedResponse(String channelFeedURL, String channelTitle, String channelURL,
                     String channelDescription, List<ItemResponse> channelItems) {
            this.channelFeedURL = channelFeedURL;
            this.channelTitle = channelTitle;
            this.channelURL = channelURL;
            this.channelDescription = channelDescription;
            this.channelItems = channelItems;
        }
    }

    // #4
    public static class ItemResponse {
        public final String itemURL;
        public final String itemTitle;
        public final String itemDescription;
        public final String itemGUID;
        public final String itemPubDate;
        public final String itemEnclosureURL;
        public final String itemEnclosureMIMEType;

        ItemResponse(String itemURL, String itemTitle, String itemDescription,
                     String itemGUID, String itemPubDate, String itemEnclosureURL,
                     String itemEnclosureMIMEType) {
            this.itemURL = itemURL;
            this.itemTitle = itemTitle;
            this.itemDescription = itemDescription;
            this.itemGUID = itemGUID;
            this.itemPubDate = itemPubDate;
            this.itemEnclosureURL = itemEnclosureURL;
            this.itemEnclosureMIMEType = itemEnclosureMIMEType;
        }
    }
}
