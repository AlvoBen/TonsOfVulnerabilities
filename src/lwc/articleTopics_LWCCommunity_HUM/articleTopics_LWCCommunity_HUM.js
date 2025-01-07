import { LightningElement,api,wire,track } from 'lwc';
import getTopics from '@salesforce/apex/ArticleDetailController_C_HUM.getTopics';
import { CurrentPageReference } from 'lightning/navigation';
import relatedTopicsLabel from '@salesforce/label/c.Community_Article_Related_Topics';
import humanaSupportTemplate from './articleTopics_LWCCommunity_HUM.html';
import go365Template from './articleTopicsGo365_LWCCommunity_HUM.html';

export default class ArticleTopics_LWCCommunity_HUM extends LightningElement {
label = {
  relatedTopicsLabel,
};

@api Go365;
@track articleUrlkv;   
@track lstTopic;
@track sfdcBaseURL;
@track URLString;
 
sfdcBaseURL=window.location.href;
URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));

/*
     * Method to display template based on Community 
     */

render() {
  return this.Go365 == true ? go365Template : humanaSupportTemplate;
}
 
/*
     * Method to get Current page reference 
     */

@wire(CurrentPageReference)
  wiredPageRef (value) {
    if (!value) {
      return;
    }
    this.articleUrlKv = value.attributes.urlName;
  }  
 
    /*
     * Method to get Topics related to Article 
     */

@wire(getTopics, {
    articleUrl: "$articleUrlKv"
})
wiredTopics({error, data}) {
try{    
    
        if(data)
        {    
          var TopicsList=[];
          var baseURL=this.URLString;
          data.forEach(function(entry) {
            
            var eachTopicObj = {};
            
            eachTopicObj.TopicId=entry.TopicId;
            eachTopicObj.TopicName=entry.Topic.Name;
            
            eachTopicObj.url= baseURL+'/s/topic/'+entry.TopicId+'/'+entry.Topic.Name;
            
            TopicsList.push(eachTopicObj);
          });
          
          this.lstTopic=TopicsList;
          
        }
}catch (error) {
    console.error("Error in wire call getTopics:" , error);
}    
}
}