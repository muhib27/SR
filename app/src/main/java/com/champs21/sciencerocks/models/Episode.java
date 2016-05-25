package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 25-May-16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("winner1")
    @Expose
    private String winner1;
    @SerializedName("winner1_district")
    @Expose
    private String winner1District;
    @SerializedName("winner2")
    @Expose
    private String winner2;
    @SerializedName("winner2_district")
    @Expose
    private String winner2District;
    @SerializedName("winner3")
    @Expose
    private String winner3;
    @SerializedName("winner3_district")
    @Expose
    private String winner3District;
    @SerializedName("question1")
    @Expose
    private String question1;
    @SerializedName("ans1")
    @Expose
    private String ans1;
    @SerializedName("question2")
    @Expose
    private String question2;
    @SerializedName("ans2")
    @Expose
    private String ans2;
    @SerializedName("winner1_occupation")
    @Expose
    private String winner1Occupation;
    @SerializedName("winner2_occupation")
    @Expose
    private String winner2Occupation;
    @SerializedName("winner3_occupation")
    @Expose
    private String winner3Occupation;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The winner1
     */
    public String getWinner1() {
        return winner1;
    }

    /**
     *
     * @param winner1
     * The winner1
     */
    public void setWinner1(String winner1) {
        this.winner1 = winner1;
    }

    /**
     *
     * @return
     * The winner1District
     */
    public String getWinner1District() {
        return winner1District;
    }

    /**
     *
     * @param winner1District
     * The winner1_district
     */
    public void setWinner1District(String winner1District) {
        this.winner1District = winner1District;
    }

    /**
     *
     * @return
     * The winner2
     */
    public String getWinner2() {
        return winner2;
    }

    /**
     *
     * @param winner2
     * The winner2
     */
    public void setWinner2(String winner2) {
        this.winner2 = winner2;
    }

    /**
     *
     * @return
     * The winner2District
     */
    public String getWinner2District() {
        return winner2District;
    }

    /**
     *
     * @param winner2District
     * The winner2_district
     */
    public void setWinner2District(String winner2District) {
        this.winner2District = winner2District;
    }

    /**
     *
     * @return
     * The winner3
     */
    public String getWinner3() {
        return winner3;
    }

    /**
     *
     * @param winner3
     * The winner3
     */
    public void setWinner3(String winner3) {
        this.winner3 = winner3;
    }

    /**
     *
     * @return
     * The winner3District
     */
    public String getWinner3District() {
        return winner3District;
    }

    /**
     *
     * @param winner3District
     * The winner3_district
     */
    public void setWinner3District(String winner3District) {
        this.winner3District = winner3District;
    }

    /**
     *
     * @return
     * The question1
     */
    public String getQuestion1() {
        return question1;
    }

    /**
     *
     * @param question1
     * The question1
     */
    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    /**
     *
     * @return
     * The ans1
     */
    public String getAns1() {
        return ans1;
    }

    /**
     *
     * @param ans1
     * The ans1
     */
    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    /**
     *
     * @return
     * The question2
     */
    public String getQuestion2() {
        return question2;
    }

    /**
     *
     * @param question2
     * The question2
     */
    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    /**
     *
     * @return
     * The ans2
     */
    public String getAns2() {
        return ans2;
    }

    /**
     *
     * @param ans2
     * The ans2
     */
    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    /**
     *
     * @return
     * The winner1Occupation
     */
    public String getWinner1Occupation() {
        return winner1Occupation;
    }

    /**
     *
     * @param winner1Occupation
     * The winner1_occupation
     */
    public void setWinner1Occupation(String winner1Occupation) {
        this.winner1Occupation = winner1Occupation;
    }

    /**
     *
     * @return
     * The winner2Occupation
     */
    public String getWinner2Occupation() {
        return winner2Occupation;
    }

    /**
     *
     * @param winner2Occupation
     * The winner2_occupation
     */
    public void setWinner2Occupation(String winner2Occupation) {
        this.winner2Occupation = winner2Occupation;
    }

    /**
     *
     * @return
     * The winner3Occupation
     */
    public String getWinner3Occupation() {
        return winner3Occupation;
    }

    /**
     *
     * @param winner3Occupation
     * The winner3_occupation
     */
    public void setWinner3Occupation(String winner3Occupation) {
        this.winner3Occupation = winner3Occupation;
    }

}