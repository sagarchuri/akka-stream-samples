import model.{Author, Tweet}
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

object TwitterStream {

  private val consumerKey = ""
  private val consumerSecret = ""
  private val accessToken = ""
  private val accessTokenSecret = ""
  // Add your own twitter credentials here

  private val conf = new ConfigurationBuilder()
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(accessToken)
    .setOAuthAccessTokenSecret(accessTokenSecret)
    .build()

  private val twitterStream = new TwitterStreamFactory(conf).getInstance()

  def start(handleTweet: Tweet => Unit)
           (language: String)
           (track: String): Unit = {

    twitterStream.addListener(new StatusListener {
      def onStallWarning(warning: StallWarning): Unit = {}

      def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

      def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

      def onStatus(status: Status): Unit =
        handleTweet(Tweet(Author(status.getUser.getName), status.getCreatedAt, status.getText))

      def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}

      def onException(ex: Exception): Unit = {}
    })

    val filter = new FilterQuery();
    filter.track(track);
    filter.language(language)

    twitterStream.filter(filter)
  }

}
