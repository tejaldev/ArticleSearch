package com.ny.search.article.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ny.search.article.R;
import com.ny.search.article.models.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Article recycler view adapter
 *
 * @author tejalpar
 */
public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TEXT_ONLY_VIEW_TYPE = 0; // articles that contains only text
    private static final int THUMBNAIL_VIEW_TYPE = 1; // articles that contains thumbnail images

    private List<Article> articleList;
    private ArticleItemClickListener itemClickListener;

    public interface ArticleItemClickListener {
        void onArticleItemClickListener(View view, Article selectedArticle);
    }

    public ArticlesAdapter(ArrayList<Article> articleList, ArticleItemClickListener itemClickListener) {
        this.articleList = articleList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (articleList.get(position).hasImage()) {
            return THUMBNAIL_VIEW_TYPE;
        }
        return TEXT_ONLY_VIEW_TYPE;
    }

    public void setMoreData(List<Article> moreItemList) {
        articleList.addAll(moreItemList);
        this.notifyItemInserted(moreItemList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case THUMBNAIL_VIEW_TYPE:
                View popularView = inflater.inflate(R.layout.image_article_row_layout, parent, false);

                //create view holder from above view
                viewHolder = new ImageArticleViewHolder(popularView);
                break;

            case TEXT_ONLY_VIEW_TYPE:
                default:
                    View textOnlyView = inflater.inflate(R.layout.text_only_article_row_layout, parent, false);

                    //create view holder from above view
                    viewHolder = new TextOnlyViewHolder(textOnlyView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)) {
            case THUMBNAIL_VIEW_TYPE:
                ImageArticleViewHolder imageArticleViewHolder = (ImageArticleViewHolder) holder;
                setUpImageArticleViewHolder(imageArticleViewHolder, position);
                break;

            case TEXT_ONLY_VIEW_TYPE:
            default:
                TextOnlyViewHolder textOnlyViewHolder = (TextOnlyViewHolder) holder;
                textOnlyViewHolder.articleTitleText.setText(articleList.get(position).headline);
                break;
        }
    }

    public class ImageArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView articleTitleText;
        public ImageView articleImageView;

        public ImageArticleViewHolder(View rootView) {
            super(rootView);

            rootView.setOnClickListener(this);
            articleTitleText = (TextView) rootView.findViewById(R.id.search_result_title);
            articleImageView = (ImageView) rootView.findViewById(R.id.search_result_image);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onArticleItemClickListener(view, articleList.get(getAdapterPosition()));
        }
    }

    public class TextOnlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView articleTitleText;

        public TextOnlyViewHolder(View rootView) {
            super(rootView);

            rootView.setOnClickListener(this);
            articleTitleText = (TextView) rootView.findViewById(R.id.search_result_title);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onArticleItemClickListener(view, articleList.get(getAdapterPosition()));
        }
    }

    private void setUpImageArticleViewHolder(ImageArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        String imageUrl = null;
        if (article.thumbnailUrl != null) {
            imageUrl = article.thumbnailUrl;
        } else if (article.wideUrl != null) {
            imageUrl = article.wideUrl;
        } else if (article.xlargeUrl != null) {
            imageUrl = article.xlargeUrl;
        }

        if (imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(Article.getAbsoluteMoviePath(imageUrl)))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.articleImageView);
        } else {
            holder.articleImageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.articleTitleText.setText(article.headline);
    }
}
