class FeedbackSearch {
  def searchService
  static mapping = { version false }
////////feedback/////////
  Integer id
  Long user_id
  Date qdate
  String qtext
  Integer feedbacktype_id
  Date adate
  String atext
  String fullstory
  Long file_id
  Integer modstatus
  Integer is_readanswer
////////user/////////////
  String user_name

  def csiSelectQuestions(lUserId,iId,sUserName,sKeyword,iType,iStatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, pers.shortname as user_name"
    hsSql.from='feedback, user, pers'
    hsSql.where="feedback.user_id=user.id and user.pers_id=pers.id"+
                ((lUserId>0)?' and feedback.user_id=:user_id':'')+
                ((iId>0)?' and feedback.id=:fid':'')+
                ((sUserName!='')?' and pers.shortname like concat("%",:persname,"%")':'')+
                ((sKeyword!='')?' and feedback.fullstory like concat("%",:keyword,"%")':'')+
                ((iType>-100)?' and feedback.feedbacktype_id=:feedbacktype_id':'')+
                ((iStatus>-100)?' and feedback.modstatus=:modstatus':'')
    hsSql.order="feedback.adate desc"

    if(lUserId>0)
      hsLong['user_id'] = lUserId
    if(iId>0)
      hsLong['fid'] = iId
    if(iType>-100)
      hsLong['feedbacktype_id'] = iType
    if(iStatus>-100)
      hsLong['modstatus'] = iStatus
    if(sUserName!='')
      hsString['persname'] = sUserName
    if(sKeyword!='')
      hsString['keyword'] = sKeyword

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'feedback.id',true,FeedbackSearch.class)
  }

}