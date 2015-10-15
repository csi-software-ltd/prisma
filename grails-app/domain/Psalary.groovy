class Psalary {
  static mapping = { version false }
  def searchService

  Long id
  Long pers_id
  Long admin_id
  Long actsalary
  Date pdate
  Date moddate
  String comment

  def afterInsert(){
    Psalary.withNewSession{
      if (id==getLastId(pers_id))
        Pers.get(pers_id).updateActsalary(actsalary).save(failOnError:true,flush:true)
    }
  }

  def beforeUpdate(){
    Psalary.withNewSession{
      if(id==getLastId(pers_id))
        Pers.get(pers_id).updateActsalary(actsalary).save(failOnError:true,flush:true)
    }
  }

  def afterDelete(){
    Psalary.withNewSession{
      Pers.get(pers_id).updateActsalary(Psalary.findByPers_idAndIdNotEqual(pers_id,id,[sort:'pdate',order:'desc'])?.actsalary?:0l).save(failOnError:true,flush:true)
    }
  }

  def csiSetPsalary(lsInrequest,lAdmin_id){
    actsalary=lsInrequest?.actsalary?:0l
    pers_id=lsInrequest.pers_id
    admin_id=lAdmin_id
    pdate=Tools.getDate(lsInrequest.pdate)
    moddate=new Date()
    comment = lsInrequest?.comment?:''
    this
  }

  def csiFindPsalary(lId){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]

    hsSql.select="*"
    hsSql.from='psalary'
    hsSql.where="1=1"+
                ((lId>0)?' and pers_id=:pers_id':' and pers_id=0')
    hsSql.order="pdate desc, moddate desc"

    if(lId>0)
      hsLong['pers_id']=lId

    searchService.fetchData(hsSql,hsLong,null,null,null,Psalary.class)
  }

  static Long getLastId(_psalary_id){
    return Psalary.findByPers_id(_psalary_id,[sort:'pdate',order:'desc'])?.id?:0l
  }
}