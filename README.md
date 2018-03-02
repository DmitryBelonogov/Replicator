# Replicator
News, full-text, and article metadata extraction in Java with Jsoup

[![Build Status](https://travis-ci.org/nougust3/Replicator.svg?branch=dev)](https://travis-ci.org/nougust3/Replicator)

## Usage
```
Loader loader = new Loader(url,
    new LoaderCallback() {
        @Override
        public void onLoaded(Article article) {
            System.out.println(article.title);
        }
        @Override
        public void onFailure() { }
    });
}
```

### Extracted data elements

- `title` - The document title
- `author` - The document author
- `content` - The main content of the document
- `image` - The main image for the document
- `tags`- Any tags or keywords
- `lang` - The language of the document
- `description` - The description of the document

### Supported languages
Russian, Arabic, Danish, German, Greek, English, Spanish, Finnish, French, Hebrew, Hungarian, Indonesian, Italian, Korean, Macedonian, Norwegian, Dutch, Swedish, Turkish, Vietnamese, Chinese.

### Demo
[replicator.nougust3.com]
