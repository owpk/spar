import { Versions } from "../config";
import { Endpoints } from "../constants";
import { CreateQuestonAnswerType, QuestionAnswerType } from "../types";
import api from "../utils/api";

export class QuestionAnswerService {
  /**
   * Request for get QuestionAnswer
   */
  static async getQuestionAnswer(data: {
    offset?: number;
    limit?: number;
  }): Promise<Array<QuestionAnswerType>> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.QUESTION_ANSWER}?${query.join("&")}`
    );
    return response.data.data;
  }
  /**
   * Request for get QuestionAnswer by id
   */
  static async getQuestionAnswerById(id: number): Promise<QuestionAnswerType> {
    const response = await api.get(`${Endpoints.QUESTION_ANSWER}/${id}`);
    return response.data.data;
  }
  /**
   * Request for update QuestionAnswer
   */
  static async updateQuestionAnswer(
    id: number,
    data: CreateQuestonAnswerType
  ): Promise<QuestionAnswerType> {
    const sendData = {
      data: data,
      version: Versions.QUESTION_ANSWER
    };
    const response = await api.put(
      `${Endpoints.QUESTION_ANSWER}/${id}`,
      sendData
    );
    return response.data.data;
  }
  /**
   * Request for create QuestionAnswer
   */
  static async createQuestionAnswer(
    data: CreateQuestonAnswerType
  ): Promise<QuestionAnswerType> {
    const sendData = {
      data: data,
      version: Versions.QUESTION_ANSWER
    };
    const response = await api.post(Endpoints.QUESTION_ANSWER, sendData);
    return response.data.data;
  }
  /**
   * Request for delete QuestionAnswerType
   */
  static async deleteQuestionAnswer(id: number): Promise<boolean> {
    const response = await api.delete(`${Endpoints.QUESTION_ANSWER}/${id}`);
    return response.data.success;
  }
}
