import api from "../utils/api";
import { Endpoints } from "../constants";
import {
  QuestionCode,
  QuestionRatingStoreType,
} from "../types";
import { Versions } from "../config";

export class QuestionRatingStoreServives {
  /**
   * Request for get BannerPlace
   */
  static async getQuestionRatingStore(data: {
    offset?: number;
    limit?: number;
  }): Promise<QuestionRatingStoreType[]> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.QUESTION_RATING_STORE}?${query.join("&")}`
    );
    return response.data.data
  }

  /**
   * Request for get BannerPlace by id
   */
  static async getQuestionRatingStoreWithId(
    id: number
  ): Promise<QuestionRatingStoreType> {
    const response = await api.get(`${Endpoints.QUESTION_RATING_STORE}/${id}`);
    return response.data.data;
    // !TODO delete when backend wil be ready
    // return bannersDemo[0]
  }

  /**
   * Request for create BannerPlace
   */
  static async createQuestionRatingStore(
    code: QuestionCode,
    data: {
      answer: string
    }
  ): Promise<QuestionRatingStoreType> {
    const sendData = {
      data,
      version: Versions.QUESTION_RATING_STORE
    };
    const response = await api.post(`${Endpoints.QUESTION_RATING_STORE}/${code}/options`, sendData);
    return response.data.data;
  }

  // /**
  //  * Request for update BannerPlace
  //  */
  static async updateQuestionRatingStore(
    code: QuestionCode,
    id: number,
    data: {
      answer: string
    }
  ): Promise<QuestionRatingStoreType> {
    const sendData = {
      data,
      version: Versions.QUESTION_RATING_STORE
    };
    const response = await api.put(
      `${Endpoints.ANSWER_RATING_STORE}/${code}/answers/${id}`,
      sendData
    );
    return response.data.data;
  }

 /**
  * delete
  */
 static async deleteQuestionRating(code: QuestionCode, answerId: number): Promise<boolean>{
   const response = await api.delete(`${Endpoints.QUESTION_RATING_STORE}/${code}/answers/${answerId}`)
   return response.data.success 
 }
}
