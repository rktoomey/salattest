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
    val _id: ObjectId = dao.insert(m).getOrElse(error("dude, this doesn't compile!"))
    println("ID returned: " + _id)
    println("Updating ...")
    // It will throw an exception if the updated failed.  See the source code.
    dao.update(MongoDBObject("_id" -> _id), m.copy(desc = "bridge player"),
      upsert = false, multi = false, wc = coll.writeConcern)
    println("Searching ...")
    val m_* = dao.findOneByID(_id).getOrElse(":(")
    println("Description should say \"bridge player\": %s".format(m_*))
  }
}

case class Mojo(@Key("_id") _id: ObjectId = new ObjectId, name: String, desc: String)

class MojoDAO(coll: MongoCollection) extends SalatDAO[Mojo, ObjectId](coll) 

