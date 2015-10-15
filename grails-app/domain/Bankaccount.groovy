class Bankaccount {
  static mapping = {
    version false
  }
  static constraints = {
    directordate(nullable:true)
    adate(nullable:true)
    opendate(nullable:true)
    closedate(nullable:true)
    ibank_open(nullable:true)
    ibank_close(nullable:true)
    saldodate(nullable:true)
    actsaldodate(nullable:true)
    actmoddate(nullable:true)
    banksaldodate(nullable:true)
  }

  Integer id
  String bank_id
  Integer company_id
  Long pers_id = 0
  Date directordate
  String schet
  String coraccount
  String anomer = ''
  Date adate
  Integer valuta_id = 857
  Integer modstatus = 1
  Date opendate
  Date closedate
  Date ibank_open
  Integer ibankterm = 0
  Date ibank_close
  Integer ibankstatus = 0
  Integer ibankblock = 0
  String ibank_comment = ''
  Integer is_bkactproc = 0
  Integer is_duplicate = 0
  Integer is_smsinfo = 0
  Integer typeaccount_id = 1
  Integer is_nosms = 0
  String smstel = ''
  String dopoffice = ''
  Integer bankclient_id = 0
  Integer saldo = 0
  Integer actsaldo = 0
  Date saldodate
  Date actsaldodate
  Date actmoddate
  Long banksaldo = 0
  Date banksaldodate

  def beforeInsert(){
    is_bkactproc = ibankstatus==1?0:is_bkactproc
  }

  def beforeUpdate(){
    is_bkactproc = ibankstatus==1?0:is_bkactproc
  }

  String toString(){
    "$schet в ${Bank.get(bank_id)?.shortname} - дата открытия ${String.format('%td.%<tm.%<tY',opendate)} - дата активации бк ${ibank_open?String.format('%td.%<tm.%<tY',ibank_open):'нет'} - дата закрытия ${closedate?String.format('%td.%<tm.%<tY',closedate):'нет'}"
  }

  Bankaccount setData(_request){
    bank_id = _request?.bank_id
    pers_id = _request?.pers_id?:0
    directordate = _request?.directordate
    coraccount = _request?.coraccount?:''
    schet = _request?.schet?_request.schet.replace('.',''):''
    anomer = _request?.anomer?:''
    adate = _request?.account_adate
    valuta_id = _request?.valuta_id?:857
    opendate = _request?.account_opendate
    closedate = _request?.account_closedate
    ibank_open = _request?.account_ibank_open
    def oBank = Bank.get(bank_id)
    ibankterm = _request?.ibankterm?:oBank?.ibankterm?:0
    ibank_close = ibankterm&&ibank_open?ibank_open+ibankterm:null
    ibank_comment = _request?.ibank_comment?:''
    is_bkactproc = _request?.is_bkactproc?:0
    if(ibank_open){
      is_duplicate = _request.is_duplicate?:0
      is_smsinfo = _request.is_smsinfo?:0
      bankclient_id = _request.bankclient_id?:0
      smstel = !is_duplicate?'':_request.smstel?:''
    }
    typeaccount_id = _request?.typeaccount_id?:1
    dopoffice = _request?.dopoffice?:''
    this
  }

  Bankaccount csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Bankaccount csiSetIbankblock(iStatus){
    ibankblock = iStatus?:0
    this
  }

  Bankaccount csiSetNoSms(iStatus){
    is_nosms = iStatus?:0
    this
  }

  Bankaccount updateModstatus(){
    modstatus = closedate?0:1
    this
  }

  Bankaccount updateIbankstatus(){
    ibankstatus = !ibank_open?0:ibankblock?-1:(ibank_close&&ibank_close<new Date().clearTime())?2:1
    this
  }
  
  Bankaccount csiSetActsaldo(iActsaldo){
    actsaldo=iActsaldo
    //actsaldodate=new Date()
    actmoddate=new Date()
    this
  }

  Bankaccount csiSetIbankComment(sComent){
    ibank_comment = sComent?:''
    this
  }

  Bankaccount csiSaldoLess(iSummaDelta){
    saldo-=iSummaDelta    
    this
  }
  
  Bankaccount csiSaldoMore(iSummaDelta){
    saldo+=iSummaDelta    
    this
  }
  
  Bankaccount csiSetSaldodate(dPaydate){
    if(dPaydate.compareTo(new Date().clearTime()) < 0)
      saldodate=new Date()
    else
      saldodate=dPaydate
       
    this
  }
  
  Bankaccount csiSetActsaldo(iActsaldo,sActsaldodate){
    if(actsaldo!=iActsaldo && actsaldodate==sActsaldodate){
      def newActsaldodate    
      def Yesterday=new Date().clearTime()
      
      while(!newActsaldodate){
        Yesterday-=1      
           
        if(Yesterday[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY || Yesterday[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY)
          if(Holiday.findByHdate(Yesterday)?.status==0)
            newActsaldodate=Yesterday
       
        if(!newActsaldodate)
          if(Yesterday[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY && Yesterday[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY)
            if(Holiday.findByHdate(Yesterday)?.status!=1)
              newActsaldodate=Yesterday          
      }
      
      actsaldodate=newActsaldodate                              
    }else{
      actsaldodate=sActsaldodate
    }        
    actsaldo=iActsaldo        
    actmoddate=new Date()
    this
  }

  Bankaccount csiSetBanksaldo(iBanksaldo,dBanksaldodate){
    banksaldo = iBanksaldo
    banksaldodate = dBanksaldodate?:new Date()
    this
  }

  def csiFindActiveAccounts(iCompanyId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='bankaccount'
    hsSql.where="modstatus=1"+
                ((iCompanyId>0)?' and company_id=:company_id':'')
    hsSql.order="bankaccount.modstatus desc, bank.name asc"

    if(iCompanyId>0)
      hsLong['company_id']=iCompanyId

    searchService.fetchData(hsSql,hsLong,null,null,null,Bankaccount.class)
  }

}
