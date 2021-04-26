package com.example.payandgo

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

private val GRAPHQL_ENDPOINT: String = "http://54.89.0.221:80/graphql"

var okHttpClient = OkHttpClient.Builder().build()
val apolloClient = ApolloClient.builder()
    .serverUrl(GRAPHQL_ENDPOINT)
    .okHttpClient(okHttpClient)
    .build()