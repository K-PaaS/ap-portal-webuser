import {Component, DoCheck, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {Router} from '@angular/router';
import {IndexCommonService} from '../userAccountMgmt/index-common.service';
import {CommonService} from '../../common/common.service';
import {HttpClient} from '@angular/common/http';
import {User, UsermgmtService} from '../../usermgmt/usermgmt.service';
import {Observable} from 'rxjs';
import {TranslateService, LangChangeEvent} from '@ngx-translate/core';
import {error} from 'util';
import {forEach} from '@angular/router/src/utils/collection';
import {CATALOGURLConstant} from '../../catalog/common/catalog.constant';

@Component({
  selector: 'app-createuser',
  templateUrl: './createuser.component.html',
  styleUrls: ['./createuser.component.css']
})
export class CreateuserComponent implements OnInit, DoCheck {

  public user: Observable<User>;

  public email: string;
  public isValidation: boolean;
  public isUsed: boolean;
  public isSendEmail: boolean;

  public translateEntities: any = [];

  constructor(private httpClient: HttpClient, public indexCommonService: IndexCommonService, private common: CommonService, private usermgmtService: UsermgmtService, 
              private translate: TranslateService, private router: Router, private log: NGXLogger) {
    this.email = '';
    this.isValidation = true;
    this.isUsed = false;

    if(this.common.getSeq() == null || this.common.getSeq() === ''){
      this.router.navigate(['/']);
    }

    this.translate.get('indexMain').subscribe((res: string) => {
      this.translateEntities = res;
    });

    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.translateEntities = event.translations.indexMain;
    });

  }

  ngOnInit() {
  }

  ngDoCheck() {

    if (this.isSendEmail != this.indexCommonService.isSendEmail) {
      this.isSendEmail = this.indexCommonService.isSendEmail;
    }
    if (this.isUsed != this.indexCommonService.isUsed) {
      this.isUsed = this.indexCommonService.isUsed;
    }

    if (this.isSendEmail) {
      this.isSendEmail = false;
      this.indexCommonService.alertMessage(this.translateEntities.alertLayer.mailSendSuccess, true);
      this.router.navigate(['/']);
    }
  }


  checkValidation() {
    this.isValidation = this.indexCommonService.checkValidation(this.email);
  }

  checkUser() {
    if (!this.isValidation) {
      let usedCount = 0;     //userId 개수 확인
      let forEachCount = 0;  //apiUrl 개수 확인
      this.common.getInfrasAll().subscribe(data => {
        let size = data.length;
        data.forEach(data => {
          let result = data['apiUri'];
          this.usermgmtService.userInfoAll(result, data['authorization']).subscribe(data2 => {
            forEachCount++;
            this.common.isLoading = false;
            let infoEnv = data2.userInfo;
            infoEnv.forEach(infoEnv => {
              let userName = infoEnv['userName'];
              if (this.email == userName) {
                usedCount++;
              }
            });

            if (forEachCount == size) {
              if (usedCount == 0) {
                this.multiUsedCreate();
              }
              if (usedCount == 1) {
                this.common.alertMessage(this.translateEntities.alertLayer.accountCreateFail, false);
              }
              if (usedCount == size) {
                this.common.alertMessage(this.translateEntities.alertLayer.memberInfoExist, false);
              }
            }
          }, error => {
            this.common.alertMessage(this.translateEntities.alertLayer.systemError, false);
          });
        });
      }, error => {
        this.common.alertMessage(this.translateEntities.alertLayer.systemError, false);
      });
    }
  }


  multiUsedCreate() {
    if (!this.isValidation) {
      this.common.getInfra(this.common.getSeq()).subscribe(infra => {
        this.indexCommonService.sendCreateEmail(infra['apiUri'], infra['authorization'], this.email);
      });
    }
  }

}


