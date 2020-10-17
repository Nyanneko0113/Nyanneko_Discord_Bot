package nyanneko.discord.api.music;

import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class WordSearch {

  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final long NUMBER_OF_VIDEOS_RETURNED = 1;
  private static YouTube youtube;

  public void world(String word, TextChannel tc) {
    try {
      youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {}
      }).setApplicationName("youtube-cmdline-search-sample").build();

      YouTube.Search.List search = youtube.search().list("id,snippet");

      search.setKey("");
      search.setQ(word);

      search.setType("video");

      search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
      search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
      SearchListResponse searchResponse = search.execute();

      List<SearchResult> searchResultList = searchResponse.getItems();

      if (searchResultList != null) {
        prettyPrint(searchResultList.iterator(), word, tc);
      }
    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
    } catch (IOException e) {
      System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, TextChannel tc) {
	EmbedBuilder em = new EmbedBuilder();
    if (!iteratorSearchResults.hasNext()) {
      em.setTitle("エラー")
             .setAuthor("動画が見つかりませんでした。")
            .setColor(Color.RED);
      tc.sendMessage(em.build()).queue();
    }

    while (iteratorSearchResults.hasNext()) {

      SearchResult singleVideo = iteratorSearchResults.next();
      ResourceId rId = singleVideo.getId();

      // Double checks the kind is video.
      if (rId.getKind().equals("youtube#video")) {
        Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");
        new PlayerManager().loadAndPlay(tc, rId.getVideoId());
      }
    }
  }
}
