package com.lamapress.animeexpo2014.axapp.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;
import com.lamapress.animeexpo2014.axapp.R;
import com.lamapress.animeexpo2014.axapp.core.News;
import com.lamapress.animeexpo2014.axapp.rss.RssFeed;
import com.lamapress.animeexpo2014.axapp.rss.RssItem;
import com.lamapress.animeexpo2014.axapp.rss.RssReader;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Anthony Lame
 * @version 1.0
 */
public class HomeFragment extends Fragment {


    public static HomeFragment newInstance(int sectionNumber){
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FlatUI.initDefaultValues(getActivity());
        FlatUI.setDefaultTheme(FlatUI.CANDY);

        View mainView = inflater.inflate(R.layout.fragment_home_activity,container,false);

        initCard();

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public void initCard(){
        final ArrayList<Card> cards = new ArrayList<Card>();


        class RssGrab extends AsyncTask<URL,Void,List<RssItem>>{


            ArrayList<RssItem> item = new ArrayList<RssItem>();

            @Override
            public List<RssItem> doInBackground(URL... urls){
                try {
                    RssFeed feed = RssReader.read(urls[0]);
                    ArrayList<RssItem> rssItems = feed.getRssItems();
                    return rssItems;
                }
                catch(SAXException sax){
                    return item;
                }
                catch(IOException io){
                    return item;
                }
            }

            @Override
            public void onPostExecute(List<RssItem> rssList){

                for(int i = 0; i < rssList.size(); i++){
                    CardInside card = new CardInside(getActivity());
                    CardTitle header = new CardTitle(getActivity(),rssList.get(i));

                    card.addCardHeader(header);

                    CardBody body = new CardBody(getActivity(),rssList.get(i));
                    card.addCardExpand(body);

                    ViewToClickToExpand viewToClickToExpand =
                            ViewToClickToExpand.builder()
                            .highlightView(false)
                            .setupCardElement(ViewToClickToExpand.CardElementUI.CARD);

                    card.setViewToClickToExpand(viewToClickToExpand);
                    cards.add(card);
                }

                CardArrayAdapter m_CardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
                CardListView listView = (CardListView)getActivity().findViewById(R.id.cards_newslist);
                listView.setAdapter(m_CardArrayAdapter);
            }
        }

        try {
            URL url = new URL("http://www.anime-expo.org/feed/");
            new RssGrab().execute(url);
        }
        catch(MalformedURLException me){
        }
    }

    public class CardInside extends Card{
        public CardInside(Context context){
            super(context);
        }
    }

    class CardBody extends CardExpand{
        RssItem item;
        public CardBody(Context context,RssItem item){
            super(context,R.layout.card_news_inner);
            this.item = item;
        }

        @Override
        public void setupInnerViewElements(ViewGroup vg, View v){
            TextView text = (TextView)v.findViewById(R.id.text_card_news_body);

            if(item!= null){
                text.setText(item.getPubDate() + " - " +Html.fromHtml(item.getDescription()));
            }
        }
    }


    class CardTitle extends CardHeader{
        RssItem rssItem;

        public CardTitle(Context context,RssItem rssItem){
            super(context,R.layout.card_news_header);
            this.rssItem = rssItem;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent,View view) {
            TextView text = (TextView) view.findViewById(R.id.text_card_news_header);
            text.setText(rssItem.getTitle());

        }
    }

}
