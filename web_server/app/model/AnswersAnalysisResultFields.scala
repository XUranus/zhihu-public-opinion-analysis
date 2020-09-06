package model


object AnswersAnalysisResultFields extends Enumeration {

  type AnswersAnalysisResultFields = Value

  val
    SentimentDistributionField,
    KeywordsField,
    RelatedKeywordsField,
    HottestQuestionsField,
    HighFrequencyAuthorsField,
    MostLikedAuthorsField,
    MostVotedAnswersField,
    HeatTrendField,
    SentimentTrendField
  = Value

}