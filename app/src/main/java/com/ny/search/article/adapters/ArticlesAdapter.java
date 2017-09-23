package com.ny.search.article.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ny.search.article.R;
import com.ny.search.article.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Article recycler view adapter
 *
 * @author tejalpar
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    private List<Article> articleList;
    private ArticleItemClickListener itemClickListener;

    public interface ArticleItemClickListener {
        void onArticleItemClickListener(View view, Article selectedArticle);
    }

    public ArticlesAdapter(ArrayList<Article> articleList, ArticleItemClickListener itemClickListener) {
        this.articleList = articleList;
        this.itemClickListener = itemClickListener;
    }

    public Article getItemAtPosition(int position) {
        return articleList.get(position);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_row_layout, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, final int position) {
        holder.articleTitleText.setText(articleList.get(position).headline);

        String imageUrl = null;
        if (articleList.get(position).thumbnailUrl != null) {
            imageUrl = articleList.get(position).thumbnailUrl;
        } else if (articleList.get(position).wideUrl != null) {
            imageUrl = articleList.get(position).wideUrl;
        } else if (articleList.get(position).xlargeUrl != null) {
            imageUrl = articleList.get(position).xlargeUrl;
        }

        if (imageUrl != null) {
            Picasso.with(holder.itemView.getContext())
                    .load(Article.getAbsoluteMoviePath(imageUrl))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.articleImageView);
        } else {
            holder.articleImageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView articleTitleText;
        public ImageView articleImageView;

        public ArticleViewHolder(View rootView) {
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
}
