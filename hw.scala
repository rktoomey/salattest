import com.mongodb._
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.global._
import com.novus.salat.annotations._


object Hi { 
  def main(args: Array[String]) { 
    println("Instantiating dao...")
    // Explicit connection doesn't work, 
    // attempts to use the public IP address
    // val conn = MongoConnection("localhost",27017)
    val conn = MongoConnection()
    val db = conn.getDB("mojotest")
    val coll = new MongoCollection(db.getCollection("mojo"))
    val dao = new MojoDAO(coll)
    println("Creating ...")
    val m: Mojo = Mojo(name = "Morris Jones", desc = "astronomer")
    println("Inserting ...")
    val _id = dao.insert(m).getOrElse("dud!")
    println("ID returned: " + _id)
    println("Updating ...")
    // No way to tell if this succeeds or not
    dao.update(MongoDBObject("_id" -> _id), MongoDBObject("desc" -> "bridge player"), 
      upsert = true, multi = true, wc = coll.writeConcern)
    println("Searching ...")
    val cursor = dao.find(ref = MongoDBObject("_id" -> _id))
    val results = cursor.toList
    println("Description should say \"bridge player\": " + results)
  }
}

case class Mojo(@Key("_id") _id: ObjectId = new ObjectId, name: String, desc: String)

class MojoDAO(coll: MongoCollection) extends SalatDAO[Mojo, ObjectId](coll) 

