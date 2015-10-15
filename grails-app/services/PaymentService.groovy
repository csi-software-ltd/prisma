import org.codehaus.groovy.grails.commons.ConfigurationHolder
import groovy.xml.MarkupBuilder

class PaymentService {

  def parsePayment(oPay){
   //set data>>
    if(oPay.frominn)
      oPay.fromcompany_id=Company.findByInn(oPay.frominn)?.id?:0
    if(oPay.toinn)
      oPay.tocompany_id=Company.findByInn(oPay.toinn)?.id?:0

    if(Company.get(oPay.fromcompany_id?:0)?.is_holding && Company.get(oPay.tocompany_id?:0)?.is_holding)
      oPay.is_internal = 1

    if (!oPay.is_internal)
      oPay.project_id = Company.get(oPay.paytype==Payment.PAY_TYPE_EXPORT?oPay.fromcompany_id:oPay.tocompany_id)?.tagproject?:0

    //set data<<
    if(oPay.kbk.size()>1||(oPay.kbk=='0'&&oPay.platperiod.size()>1)){//1>>
      oPay.paycat = Payment.PAY_CAT_BUDG

      def bFindKbkType = 0
      def iKbkrazdel_id = Kbkrazdel.get(Kbk.findByKbksearchLike('%'+(oPay.kbk.size()>1?oPay.kbk:'none')+'%')?.kbkrazdel_id?:0)?.id?:0
      if(iKbkrazdel_id){
        bFindKbkType = 1
        oPay.kbkrazdel_id = iKbkrazdel_id
      }
      def bFindCompany = 0
      if(oPay.paytype==Payment.PAY_TYPE_EXPORT){//исходящий
        if(Company.get(oPay.fromcompany_id?:0)?.is_holding)
          bFindCompany = 1
      }else if(Company.get(oPay.tocompany_id?:0)?.is_holding){
        bFindCompany = 1
      }

      if(bFindKbkType && bFindCompany)
        oPay.modstatus = 2
      else
        oPay.modstatus = 1

    //1<<
    }else{
      def bFindCompany = 0

      if(Company.get(oPay.fromcompany_id?:0))
        bFindCompany++
      if(Company.get(oPay.tocompany_id?:0))
        bFindCompany++

      //3>>
      def bPersaccount=0
      if(oPay.paytype==Payment.PAY_TYPE_EXPORT){
        for(oPersacount in Persaccount.findAllByBank_idAndPaccount(oPay.tobankbik,oPay.toaccount)){
          if(Compers.findByCompany_idAndPers_idAndModstatus(oPay.fromcompany_id?:0,oPersacount.pers_id,1)){
            bPersaccount = 1
            oPay.pers_id = oPersacount.pers_id
          }
        }

        if(bPersaccount) oPay.paycat = Payment.PAY_CAT_PERS
        if(Company.get(oPay.fromcompany_id?:0)?.is_holding==1&&bPersaccount)
          oPay.modstatus = 2
      }
    }

    def oPayrequest

    if(!oPay.payrequest_id){
      if(oPay.paycat==Payment.PAY_CAT_BUDG){
        oPayrequest=Payrequest.findByFrominnAndSummaAndPaytypeAndModstatusAndPaycat(oPay.frominn,oPay.summa,1,2,2)
      }else if(oPay.paycat==Payment.PAY_CAT_PERS){
        oPayrequest=Payrequest.findByFrominnAndSummaAndPaytypeAndModstatusAndPaycatAndPers_id(oPay.frominn,oPay.summa,1,2,3,oPay.pers_id)
      }else if(oPay.toinn){
        oPayrequest=Payrequest.findByFrominnAndSummaAndModstatusAndPaycatInListAndToinn(oPay.frominn,oPay.summa,2,[1,4],oPay.toinn)
        if (!oPayrequest && oPay.paytype == Payment.PAY_TYPE_IMPORT) oPayrequest = Payrequest.findByFrominnAndSummaAndModstatusGreaterThanAndPaycatInListAndToinn(oPay.toinn,oPay.summa,2,[1,4],oPay.frominn)
      }
    }
    if(oPayrequest){
      oPay.payrequest_id = oPayrequest.id
      if (!oPay.paycat) oPay.paycat = oPayrequest.paycat
      if (!oPay.fromcompany_id) oPay.fromcompany_id = oPayrequest.fromcompany_id
      if (!oPay.tocompany_id) oPay.tocompany_id = oPayrequest.tocompany_id

      oPay.agreementtype_id = oPayrequest.agreementtype_id
      oPay.agreement_id = oPayrequest.agreement_id

      oPay.tagcomment = oPayrequest.tagcomment
      oPay.client_id = oPayrequest.client_id
      oPay.subclient_id = oPayrequest.subclient_id
      oPay.project_id = oPayrequest.project_id
      oPay.expensetype_id = oPayrequest.expensetype_id
      oPay.car_id = oPayrequest.car_id
      oPay.agentagr_id = oPayrequest.agentagr_id
      oPay.agent_id = oPayrequest.agent_id
      oPay.modstatus = 2
      oPayrequest.confirmincome(Payment.PAY_TYPE_EXPORT?1:3).csiSetModstatus(3).save(flush:true,failOnError:true)
    }
    if (!oPay.payrequest_id && !oPay.paycat){
      if(oPay.summa<Tools.getIntVal(Dynconfig.findByName('payment.bankcat.max.summa')?.value,2500) && oPay.paytype==Payment.PAY_TYPE_EXPORT && oPay.is_internal==0 && oPay.destination=='' && Company.get(oPay.tocompany_id?:0)?.is_bank){
        oPay.paycat = Payment.PAY_CAT_BANK
        oPay.expensetype_id = Tools.getIntVal(Dynconfig.findByName('payment.bankcat.expensetype.default')?.value,43)
      } else if(Paykeword.list().find{ oPay.destination.toLowerCase().matches(".*${it.keyword}.*") }) oPay.paycat = Payment.PAY_CAT_OTHER
      if(oPay.paycat&&Company.get(oPay.fromcompany_id)&&Company.get(oPay.tocompany_id)) oPay.modstatus = 2
    }

    oPay.save(flush:true,failOnError:true)
  }

  def parsePaymentByHand(oPay){
    if(oPay.frominn)
      oPay.fromcompany_id = Company.findByInnAndModstatus(oPay.frominn,1)?.id?:0
    if(oPay.toinn)
      oPay.tocompany_id = Company.findByInnAndModstatus(oPay.toinn,1)?.id?:0
    def oFromcompany = Company.get(oPay.fromcompany_id?:0)
    def oTocompany = Company.get(oPay.tocompany_id?:0)

    if(oFromcompany?.is_holding==1 && oTocompany?.is_holding==0) oPay.paytype = 1
    else if(oFromcompany?.is_holding==0 && oTocompany?.is_holding==1) oPay.paytype = 2
    if(oFromcompany?.is_holding && oTocompany?.is_holding) oPay.is_internal = 1
    else oPay.is_internal = 0

    if(oPay.paycat==Payment.PAY_CAT_BUDG){//1>>
      def bFindKbkType = 0
      if(oPay.kbkrazdel_id)
        bFindKbkType = 1

      def bFindCompany = 0
      if(oPay.paytype==Payment.PAY_TYPE_EXPORT){//исходящий
        if(oFromcompany?.is_holding)
          bFindCompany = 1
      }else if(oTocompany?.is_holding){
        bFindCompany = 1
      }

      if(bFindKbkType && bFindCompany)
        oPay.modstatus = 2
      else
        oPay.modstatus = 1

    //1<<
    }else if(oPay.paycat==Payment.PAY_CAT_AGR || oPay.paycat==Payment.PAY_CAT_OTHER || oPay.paycat==Payment.PAY_CAT_ORDER){
      def bFindCompany = 0

      if(oFromcompany)
        bFindCompany++
      if(oTocompany)
        bFindCompany++

      def bAgreement = false
      if(oPay.paycat==Payment.PAY_CAT_AGR){
        if(bFindCompany==2){
          switch(oPay.agreementtype_id){
            case 1: if(License.get(oPay.agreement_id)) bAgreement=true; break
            case 2: if(Space.get(oPay.agreement_id)) bAgreement=true; break
            case 3: if(Kredit.get(oPay.agreement_id)) bAgreement=true; break
            case 4: if(Lizing.get(oPay.agreement_id)) bAgreement=true; break
            case 5: if(Agentagr.get(oPay.agreement_id)) bAgreement=true; break
            case 6: if(Cession.get(oPay.agreement_id)) bAgreement=true; break
            case 7: if(Trade.get(oPay.agreement_id)) bAgreement=true; break
            case 8: if(Service.get(oPay.agreement_id)) bAgreement=true; break
            case 9: if(Smr.get(oPay.agreement_id)) bAgreement=true; break
            case 10: if(Loan.get(oPay.agreement_id)) bAgreement=true; break
            case 11: if(Bankdeposit.get(oPay.agreement_id)) bAgreement=true; break
            case 12: if(Finlizing.get(oPay.agreement_id)) bAgreement=true; break
            case 13: if(Indeposit.get(oPay.agreement_id)) bAgreement=true; break
          }
        }

        if(bFindCompany==2 && bAgreement)
          oPay.modstatus = 2
        else
          oPay.modstatus = 1
      }else if(oPay.paycat==Payment.PAY_CAT_OTHER || oPay.paycat==Payment.PAY_CAT_ORDER){
        if(bFindCompany==2)
          oPay.modstatus = 2
        else oPay.modstatus = 1
      }
    }else if(oPay.paycat==Payment.PAY_CAT_BANK){
      if(oPay.paytype==Payment.PAY_TYPE_EXPORT && oPay.is_internal==0 && oTocompany?.is_bank) oPay.modstatus = 2
      else oPay.modstatus = 1
    }else if(oPay.paycat==Payment.PAY_CAT_PERS){
      if(oPay.paytype==Payment.PAY_TYPE_EXPORT){
        def bFromCompany = 0
        def bPersaccount = 0
        def bPers = 0

        if(oFromcompany?.is_holding==1)
          bFromCompany = 1

        for(oPersacount in Persaccount.findAllByBank_idAndPaccount(oPay.tobankbik,oPay.toaccount)){
          if(Compers.findByCompany_idAndPers_id(oPay.fromcompany_id?:0,oPersacount.pers_id)){
            bPersaccount = 1
            if(!oPay.pers_id) oPay.pers_id = oPersacount.pers_id
          }
        }

        if(bPersaccount && bFromCompany) oPay.modstatus = 2
        else oPay.modstatus = 1

      } else oPay.modstatus = 1
    } else oPay.modstatus = 1

    def oPayrequest = Payrequest.get(oPay.payrequest_id)
    if(!oPay.payrequest_id){
      if(oPay.paycat==Payment.PAY_CAT_BUDG){
        oPayrequest=Payrequest.findByFrominnAndSummaAndPaytypeAndModstatusAndPaycat(oPay.frominn,oPay.summa,1,2,2)  
      }else if(oPay.paycat==Payment.PAY_CAT_PERS){
        oPayrequest=Payrequest.findByFrominnAndSummaAndPaytypeAndModstatusAndPaycatAndPers_id(oPay.frominn,oPay.summa,1,2,3,oPay.pers_id)          
      }else if(oPay.toinn){
        oPayrequest=Payrequest.findByFrominnAndSummaAndModstatusAndPaycatInListAndToinn(oPay.frominn,oPay.summa,2,[1,4],oPay.toinn)
        if (!oPayrequest && oPay.is_internal == 1 && oPay.paytype == Payment.PAY_TYPE_IMPORT) oPayrequest = Payrequest.findByFrominnAndSummaAndModstatusGreaterThanAndPaycatInListAndToinn(oPay.toinn,oPay.summa,2,[1,4],oPay.frominn)
      }
    }

    if(oPayrequest){
      oPay.payrequest_id = oPayrequest.id
      if (!oPay.paycat) oPay.paycat = oPayrequest.paycat
      if (!oPay.fromcompany_id) oPay.fromcompany_id = oPayrequest.fromcompany_id
      if (!oPay.tocompany_id) oPay.tocompany_id = oPayrequest.tocompany_id

      if(!oPay.agreementtype_id) oPay.agreementtype_id = oPayrequest.agreementtype_id
      if(!oPay.agreement_id) oPay.agreement_id = oPayrequest.agreement_id
      if(!oPay.tagcomment) oPay.tagcomment = oPayrequest.tagcomment
      if(!oPay.client_id) oPay.client_id = oPayrequest.client_id
      if(!oPay.subclient_id) oPay.subclient_id = oPayrequest.subclient_id
      if(!oPay.project_id) oPay.project_id = oPayrequest.project_id
      if(!oPay.expensetype_id) oPay.expensetype_id = oPayrequest.expensetype_id
      if(!oPay.car_id) oPay.car_id = oPayrequest.car_id
      if(!oPay.agentagr_id) oPay.agentagr_id = oPayrequest.agentagr_id
      if(!oPay.agent_id) oPay.agent_id = oPayrequest.agent_id
      oPay.modstatus = 2
      oPayrequest.confirmincome(Payment.PAY_TYPE_EXPORT?1:3).csiSetModstatus(3).csiSetPayrequestTag(oPay,true,0).save(flush:true,failOnError:true)
    }

    oPay.save(flush:true,failOnError:true)
  }

  def importXmlDataFrom1S(_xml){   
    if (!_xml)
      throw new Exception ('No file')
    if (_xml.getContentType() != "plain/xml" && _xml.getContentType() != "application/xml" && _xml.getContentType() != "text/xml" )
      throw new Exception ('Not supported file type')
    
    InputStreamReader isr = new InputStreamReader(_xml.getInputStream(), "windows-1251")        
    def records
    def iEncoding=0
    try{
      log.debug('try windows-1251')
      records = new XmlSlurper().parse(isr)  
    }catch(Exception e){
      log.debug('try utf-8')
      iEncoding=1      
    }
    if(iEncoding){
      try{
        isr = new InputStreamReader(_xml.getInputStream(), "utf-8")
        records = new XmlSlurper().parse(isr)
      }catch(Exception e){
        log.debug('try utf-8 remove BOM')       
        iEncoding=2
      }  
    }
    if(iEncoding==2){
      BufferedReader reader
      try{       
        isr = new InputStreamReader(_xml.getInputStream(), "utf-8")                
        reader = new BufferedReader(isr);
        
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        def sXml=out.toString()
        
        sXml=sXml.replace("\uFEFF", "");                                      
        
        records = new XmlSlurper().parseText(sXml)               
      }catch(Exception e){  
        log.debug('no encoding found')
        iEncoding=3
      }finally{
        reader.close();
      }    
    }
    isr.close()
    
    if (records.@Назначение.text()!='PRISMA'){   
      log.debug('target')
      throw new Exception ('Invalid destination')
    }       
    
    parseXmlPaymentsData(records)    
  }
  private def parseXmlPaymentsData(records){    
    def result = [complete:0,total:records.Платежи.Платеж.size()?:0,notimport:[]]
    records.Платежи.Платеж.each{      
      try {                        
        def oPayment=Payment.findById1c(it.@НомерПлатежа1С?.text())
        if(oPayment){                 
          if (oPayment.csiUpdatePayment(it)?.save(failOnError:true,flush:true)){
            result.complete++
            //parsePayment(oPayment)                     
          }
        }else{ 
          oPayment=new Payment()
          if (oPayment.linkPayment(it)?.save(failOnError:true,flush:true)){
            result.complete++
            parsePayment(oPayment)          
          }                   
        }
      } catch(Exception e) {
          log.debug('error on update Payment in PaymentService:parseXmlPaymentsData '+it.@НомерПлатежа1С?.text()+'\n'+e.toString())
          result.notimport << it.@НомерПлатежа1С?.text()
      }
    }
 
    return result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported payment: '+result.notimport.toString()):'')
  } 
  def csiSetSaldo(iId){
    def oPayment=Payment.get(iId)
    if (!oPayment) {
      throw new Exception ('No data')
    }
    def result = 0
    synchronized(this) {
      Payment.withTransaction { status ->
        try {
          if(oPayment.modstatus==2){
            if(oPayment.paytype==Payment.PAY_TYPE_EXPORT&&oPayment.fromcompany_id==oPayment.tocompany_id){
              def oFromBankaccount = Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.frombankbik,oPayment.fromaccount,oPayment.fromcompany_id?:0)
              def oToBankaccount = Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.tobankbik,oPayment.toaccount,oPayment.tocompany_id?:0)
              if(oFromBankaccount&&oToBankaccount){
                def iSummaDelta = oPayment.summa - oPayment.summaold
                oFromBankaccount.csiSaldoLess(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oToBankaccount.csiSaldoMore(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oPayment.summaold = oPayment.summa
                oPayment.finstatus = 1
                oPayment.save(flush:true,failOnError:true)
                result = 1
              } else {
                result = 2
              }
            } else if(oPayment.paytype==Payment.PAY_TYPE_EXPORT){
              def oBankaccount=Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.frombankbik,oPayment.fromaccount,oPayment.fromcompany_id?:0)
              if(oBankaccount){
                def iSummaDelta=oPayment.summa - oPayment.summaold
                oBankaccount.csiSaldoLess(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oPayment.summaold = oPayment.summa
                oPayment.finstatus = 1
                oPayment.save(flush:true,failOnError:true)
                result = 1
              }else{
                result = 2
              }
            }else if(oPayment.paytype==Payment.PAY_TYPE_IMPORT){
              def oBankaccount=Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.tobankbik,oPayment.toaccount,oPayment.tocompany_id?:0)
              if(oBankaccount){
                def iSummaDelta=oPayment.summa - oPayment.summaold
                oBankaccount.csiSaldoMore(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oPayment.summaold = oPayment.summa
                oPayment.finstatus = 1
                oPayment.save(flush:true,failOnError:true)
                result = 1
              }else{
                result = 2
              }
            }
          }
          status.flush()
        } catch(Exception e) {
          log.debug("PaymentService:csiSetSaldo:\n"+e.toString())
          status.setRollbackOnly()
        }
      }
    }
    return result
  }

  def csiSetSaldoAll(){
    def result = [complete:0,total:0,notdone:[]]
    synchronized(this) {
      def lsPayment=Payment.findAllByModstatusAndFinstatus(2,0)
      result.total=lsPayment.size()

      Payment.withTransaction { status ->
        for(oPayment in lsPayment){
          def oSavepoint = status.createSavepoint()

          try {
            if(oPayment.paytype==Payment.PAY_TYPE_EXPORT&&oPayment.fromcompany_id==oPayment.tocompany_id){
              def oFromBankaccount = Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.frombankbik,oPayment.fromaccount,oPayment.fromcompany_id?:0)
              def oToBankaccount = Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.tobankbik,oPayment.toaccount,oPayment.tocompany_id?:0)
              if(oFromBankaccount&&oToBankaccount){
                def iSummaDelta = oPayment.summa - oPayment.summaold
                oFromBankaccount.csiSaldoLess(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oToBankaccount.csiSaldoMore(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oPayment.summaold = oPayment.summa
                oPayment.finstatus = 1
                oPayment.save(flush:true,failOnError:true)

                result.complete++
                status.releaseSavepoint(oSavepoint)
              } else {
                result.notdone << oPayment.platnumber
                status.rollbackToSavepoint(oSavepoint)
              }
            } else if(oPayment.paytype==Payment.PAY_TYPE_EXPORT){
              def oBankaccount=Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.frombankbik,oPayment.fromaccount,oPayment.fromcompany_id?:0)
              if(oBankaccount){
                def iSummaDelta=oPayment.summa - oPayment.summaold
                oBankaccount.csiSaldoLess(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oPayment.summaold = oPayment.summa
                oPayment.finstatus = 1
                oPayment.save(flush:true,failOnError:true)

                result.complete++
                status.releaseSavepoint(oSavepoint)
              }else{
                result.notdone << oPayment.platnumber
                status.rollbackToSavepoint(oSavepoint)
              }
            }else if(oPayment.paytype==Payment.PAY_TYPE_IMPORT){
              def oBankaccount=Bankaccount.findByBank_idAndSchetAndCompany_id(oPayment.tobankbik,oPayment.toaccount,oPayment.tocompany_id?:0)
              if(oBankaccount){
                def iSummaDelta=oPayment.summa - oPayment.summaold
                oBankaccount.csiSaldoMore(iSummaDelta).csiSetSaldodate(oPayment.paydate).save(flush:true,failOnError:true)
                oPayment.summaold = oPayment.summa
                oPayment.finstatus = 1
                oPayment.save(flush:true,failOnError:true)

                result.complete++
                status.releaseSavepoint(oSavepoint)
              }else{
                result.notdone << oPayment.platnumber
                status.rollbackToSavepoint(oSavepoint)
              }
            }
          } catch(Exception e) {
            log.debug('error on PaymentService in csiSetSaldoAll: platnumber: '+oPayment.platnumber+',paydate: '+String.format('%td.%<tm.%<tY',oPayment.paydate)+'\n'+e.toString())
            result.notdone << oPayment.platnumber
            status.rollbackToSavepoint(oSavepoint)
          }
        }
      }
    }
    result.snotdone=result.notdone.join(', ')

    return result
  }

  void createInnerProjectOperation(_request){
    Indepositproject.withTransaction { status ->
      try {
        synchronized(this) {
          Indepositproject prjoperation = new Indepositproject(indeposit_id:_request.deposit_id,operationdate:_request.operationdate).csiSetProject(_request.project_from).csiSetSumma(-_request.summa).csiSetIsTransfer().save(failOnError:true)
          new Indepositproject(indeposit_id:_request.deposit_id,operationdate:_request.operationdate).csiSetProject(_request.project_to).csiSetSumma(_request.summa).csiSetIsTransfer().csiSetRelated(prjoperation.id).save(failOnError:true)
          status.flush()
        }
      } catch(Exception e) {
        log.debug("PaymentService:createInnerProjectOperation:\n"+e.toString())
        status.setRollbackOnly()
        throw e
      }
    }
  }

  void doRequest(Payrequest _req){
    if(_req.paytype!=1&&_req.paytype!=5) return
    switch (_req.paycat){
      case 1: doAgrRequest(_req); break;
      case 2: doBRequest(_req); break;
      case 3: doPersRequest(_req); break;
    }
  }
  void undoRequest(Payrequest _req){
    if(_req.paytype!=1&&_req.paytype!=5) return
    switch (_req.paycat){
      case 1: undoAgrRequest(_req); break;
      case 2: undoBRequest(_req); break;
      case 3: undoPersRequest(_req); break;
    }
  }
  void doAgrRequest(Payrequest _req){
    switch (_req.agreementtype_id){
      case 1: Licplanpayment.findByLicense_idAndPaydate(_req.agreement_id,_req.paydate)?.csiSetModstatus(2)?.save(); break;
      case 3: Kreditpayment.get(_req.agrpayment_id)?.updatepaiddata(_req.summa,2,_req.is_dop,new Date())?.computeModstatus()?.save(); break;
    }
  }
  void undoAgrRequest(Payrequest _req){
    switch (_req.agreementtype_id){
      case 1: Licplanpayment.findByLicense_idAndPaydate(_req.agreement_id,_req.paydate)?.csiSetModstatus(1)?.save(); break;
      case 3: Kreditpayment.get(_req.agrpayment_id)?.updatepaiddata(-_req.summa,1,_req.is_dop,null)?.computeModstatus()?.save(); break;
    }
  }
  void doPersRequest(Payrequest _req){
    if(!_req.platperiod) return
    Salarycomp.findByMonthAndYearAndPers_idAndCompany_id(_req.platperiod.split('\\.')[0],_req.platperiod.split('\\.')[1],_req.pers_id,_req.fromcompany_id)?.updatePaidstatuses(2,Persaccount.findByPers_idAndPaccount(_req.pers_id,_req.toaccount.replace('\\.',''))?.is_main)?.csiSetPaydate(new Date())?.save()
  }
  void undoPersRequest(Payrequest _req){
    if(!_req.platperiod) return
    Salarycomp.findByMonthAndYearAndPers_idAndCompany_id(_req.platperiod.split('\\.')[0],_req.platperiod.split('\\.')[1],_req.pers_id,_req.fromcompany_id)?.updatePaidstatuses(1,Persaccount.findByPers_idAndPaccount(_req.pers_id,_req.toaccount.replace('\\.',''))?.is_main)?.csiSetPaydate(null)?.save()
  }
  void doBRequest(Payrequest _req){
    if(!_req.platperiod) return
    if (_req.tax_id<6) Salarycomp.findByMonthAndYearAndIs_persAndCompany_id(_req.platperiod.split('\\.')[0],_req.platperiod.split('\\.')[1],0,_req.fromcompany_id)?.csiSetPaidstatus(2)?.csiSetPaydate(new Date())?.save()
    else Taxpayment.findByMonthAndYearAndCompany_idAndTax_id(_req.platperiod.split('\\.')[0],_req.platperiod.split('\\.')[1],_req.fromcompany_id,_req.tax_id)?.csiSetPaystatus(2)?.csiSetPaydate(new Date())?.save()
  }
  void undoBRequest(Payrequest _req){
    if(!_req.platperiod) return
    if (_req.tax_id<6) Salarycomp.findByMonthAndYearAndIs_persAndCompany_id(_req.platperiod.split('\\.')[0],_req.platperiod.split('\\.')[1],0,_req.fromcompany_id)?.csiSetPaidstatus(1)?.csiSetPaydate(null)?.save()
    else Taxpayment.findByMonthAndYearAndCompany_idAndTax_id(_req.platperiod.split('\\.')[0],_req.platperiod.split('\\.')[1],_req.fromcompany_id,_req.tax_id)?.csiSetPaystatus(1)?.csiSetPaydate(null)?.save()
  }

  void updatedeal(Deal _deal, ArrayList payrequestlist){
    Integer dealId = _deal.computeDates(payrequestlist).updateComission(payrequestlist.sum{ it.computeComission() }).updateSubComission(payrequestlist.sum{ it.computeSubComission() }).updateRepayment(payrequestlist.sum{ it.clientcommission }).updateRetComission(payrequestlist.sum{ it.is_clientcommission||it.is_midcommission?it.clientdelta:0.0g }).updateIncome(payrequestlist.sum{ it.computeIncome() }).updateOutlay(payrequestlist.sum{ it.computeOutlay() }).save(flush:true,failOnError:true)?.id?:0
    payrequestlist.each{ it.csiSetDeal_id(dealId).save(flush:true) }
  }
}