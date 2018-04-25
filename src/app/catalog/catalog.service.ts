import { Injectable } from '@angular/core';
import {CommonService} from '../common/common.service';
import {NGXLogger} from "ngx-logger";

@Injectable()
export class CatalogService {
  COMMONAPI : string = '/commonapi';
  V2_URL : string = '/v2';
  DEVELOPGET : string = this.COMMONAPI + this.V2_URL +'/developpacks';
  TEMPLATEGET : string = this.COMMONAPI + this.V2_URL + '/starterpacks';
  SEARCHGET : string = this.COMMONAPI + this.V2_URL + '/packs';
  HISTORYGET : string = this.COMMONAPI + this.V2_URL + '/history';


  developments : Array<Development> = Array<Development>();
  templates : Array<Template> = Array<Template>();
  recentpacks : Array<Development|Template> = Array<Development|Template>();
  constructor(private common: CommonService, private log: NGXLogger) {
  }

  developInit()
  {
    this.common.doGET(this.DEVELOPGET, null).subscribe(data => {
      this.DevelopmentInit(data['list']);
    });
    this.common.doGET(this.TEMPLATEGET, null).subscribe(data =>{
        this.TemplateInit(data['list']);
    })
    this.common.doGET(this.HISTORYGET, null).subscribe(data =>{
     let lenght = data['list'].length < 4 ? data['list'].length : 4;
      for(let i =0; i < lenght; i++) {
        let dev = data['list'][i];
        this.recentpacks[i] = dev;
        console.log(this.recentpacks[i]);
      }

    })
  }

  Search(searchKeyword : string)
  {
      this.common.doGET(this.SEARCHGET+'?searchKeyword='+searchKeyword, null).subscribe(data => {this.DevelopmentInit(data['BuildPackList']); this.TemplateInit(data['TemplateList']);});
  }

  DevelopmentInit(data : any)
  {
    console.log(data);
    this.developments = new Array<Development>();
    for(let i = 0 ; i < data.length ; i++) {
      this.developments[i] = data[i];
    }
  }

  TemplateInit(data : any)
  {
    console.log(data);
    this.templates = new Array<Template>();
    for(let i = 0 ; i < data.length ; i++) {
      this.templates[i] = data[i];
    }
  }
}

export class Development
{
  appSampleFileName : string;
  appSampleFilePaht : string;
  appSampleFilePath : string;
  appSampleFileSize : string;
  buildPackName : string;
  classification : string;
  classificationSummary : string;
  classificationValue : string;
  created : string;
  description : string;
  lastmodified : string;
  name : string;
  no : string;
  summary : string;
  thumbImgName : string;
  thumbImgPath : string;
  useYn : string;
  userId : string;
}

export class Template
{
  buildPackCategoryNo : string;
  classification : string;
  classificationSummary : string;
  classificationValue : string;
  created : string;
  description : string;
  lastmodified : string;
  name : string;
  no : string;
  servicePackCategoryNoList : string;
  summary : string;
  thumbIimgName : string;
  thumbImgPath : string;
  useYn : string;
  userId : string;
}