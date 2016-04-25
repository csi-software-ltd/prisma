class Cession {
  static mapping = {
    version false
  }
  static constraints = {
  }
  private enum Historyfields {
    ENDDATE, SUMMA, RESPONSIBLE
  }

  Integer id
  Integer cessionvariant = 1
  Integer cessiontype
  Integer changetype
  Integer paytype
  Integer zalogstatus = 1
  String cedent = ''
  Integer cedentcompany = 0
  Integer cessionary
  String cbank_id
  Integer debtor
  String anumber
  Date adate
  Date inputdate = new Date()
  Date enddate
  String description
  Integer modstatus = 1
  String comment
  BigDecimal summa
  Integer valuta_id
  Integer agr_id
  Integer client_id = 0
  Integer is_dirsalary = 1
  Long responsible
  BigDecimal maindebt
  BigDecimal procdebt
  Integer is_debtfull
  String procdebtperiod

  def transient admin_id = 0
  def transient dopagrcomment = ''

  def afterInsert(){
    new Cessionhist(cession_id:id,admin_id:admin_id,dopagrcomment:dopagrcomment).setData(properties).save(failOnError:true)
    if (cessionvariant==1) Kredit.get(agr_id)?.csiSetCessionstatus(id)?.csiSetCreditor(cessiontype==2?cessionary:0)?.csiSetCbank(cessiontype==2?cbank_id:'')?.csiSetCurclient(client_id,adate)?.save(failOnError:true)
    else if (cessionvariant==2) Lizing.get(agr_id)?.csiSetCessionstatus(id)?.csiSetCreditor(cessiontype==2?cessionary:0)?.csiSetCbank(cessiontype==2?cbank_id:'')?.save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Cessionhist(cession_id:id,admin_id:admin_id,dopagrcomment:dopagrcomment).setData(properties).save(failOnError:true)
    if(cessionvariant==1&&isDirty('adate')){ def _cdate = getPersistentValue('adate'); Agentperiod.withNewSession{ Agentperiod.findByKredit_idAndStartdate(agr_id,_cdate)?.csiSetStartdate(adate)?.save(flush:true); Agentperiod.findByKredit_idAndEnddate(agr_id,_cdate-1)?.csiSetEnddate(adate-1)?.save(flush:true) }}
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  Cession setData(_request){
    if (cessionvariant==1){
      def oKredit = Kredit.get(_request.kredit_id)
      paytype = oKredit.kredtype
      zalogstatus = oKredit.zalogstatus
      cedent = oKredit.bank_id
      debtor = oKredit.client
      agr_id = oKredit?.id
    } else if (cessionvariant==2) {
      def oLizing = Lizing.get(_request.lizing_id)
      paytype = oLizing.lizsort
      cedentcompany = oLizing.arendodatel
      debtor = oLizing.arendator
      agr_id = oLizing.id
    }
    cessiontype = _request.cessiontype
    def oCessionary = Company.findByNameOrInn(_request.cessionary,_request.cessionary)
    cessionary = oCessionary.id
    cbank_id = _request.cbank_id
    changetype = oCessionary.is_holding==0?2:Company.get(debtor).is_holding==0?1:3
    anumber = _request.anumber
    adate = _request.adate
    enddate = _request.enddate
    description = _request.description?:''
    comment = _request.comment?:''
    valuta_id = _request.valuta_id?:857
    responsible = _request.responsible?:0l
    maindebt = _request.maindebt?:0.0g
    procdebt = _request.procdebt?:0.0g
    summa = maindebt + procdebt
    is_debtfull = _request.is_debtfull?:0
    procdebtperiod = _request.procdebtperiod?:''
    this
  }

  Cession csiSetClient_id(iClientId){
    client_id = cessionvariant==2||iClientId==-1 ? 0 : iClientId ?: client_id
    this
  }

  Cession csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Cession csiSetDopComment(sComment){
    dopagrcomment = sComment?:''
    this
  }

  Cession csiSetDirSalary(iStatus,iAccess){
    is_dirsalary = !iAccess?is_dirsalary:iStatus?:0
    this
  }

  Boolean isHaveDirty (){ return Cession.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

}