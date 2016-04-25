class PersaccountSearch {
  def searchService
  static mapping = { version false }
//////////////persaccount/////////////////
  Long id
  Long pers_id
  String bank_id
  String nomer
  String paccount
  String validmonth
  String validyear
  String pin
  Integer is_main
  Integer modstatus
  Date inputdate
  Date moddate
  Long admin_id
//////////////pers////////////////////////
  String shortname
//////////////bank////////////////////////
  String name
  Integer is_license

////////////////////////////////////////////////////////////
  String toStringBankname(){
    "$bank_id - $name ${!is_license?'(Отозвана лицензия)':''}"
  }

  def csiSelectPaccounts(hsInrequest,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsInt=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from="persaccount join pers on (persaccount.pers_id=pers.id) join bank on (persaccount.bank_id=bank.id)"
    hsSql.where="1=1"+
      (hsInrequest?.pers_name?' AND pers.shortname like concat("%",:persname,"%")':'')+
      (hsInrequest?.bankname?' AND (bank.name like concat("%",:bankname,"%") or bank.id like concat("%",:bankname,"%"))':'')+
      (hsInrequest?.paccount?' AND persaccount.paccount like concat("%",:paccount,"%")':'')+
      ((hsInrequest?.modstatus?:0)>-100?' AND persaccount.modstatus=:modstatus':'')+
      ((hsInrequest?.is_main?:0)>-100?' AND persaccount.is_main=:is_main':'')
    hsSql.order = hsInrequest?.sort?'pers.shortname asc':'bank.name asc, pers.shortname asc'

    if(hsInrequest?.pers_name)
      hsString['persname'] = hsInrequest?.pers_name
    if(hsInrequest?.bankname)
      hsString['bankname'] = hsInrequest?.bankname
    if(hsInrequest?.paccount)
      hsString['paccount'] = hsInrequest?.paccount
    if((hsInrequest?.modstatus?:0)>-100)
      hsInt['modstatus'] = hsInrequest?.modstatus?:0
    if((hsInrequest?.is_main?:0)>-100)
      hsInt['is_main'] = hsInrequest?.is_main?:0

    searchService.fetchDataByPages(hsSql,null,null,hsInt,hsString,null,null,iMax,iOffset,'persaccount.id',true,PersaccountSearch.class)
  }
}