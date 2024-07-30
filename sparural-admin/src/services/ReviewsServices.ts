import api from "../utils/api";
import { Endpoints } from "../constants";
import {
  ReviewsType
} from "../types";
import { toQueryString } from "../utils/helpers";

/*
  {
    id: 3,
    data: `12.122021`,
    user: "Фамилия Имя Отчество",
    rate: 3,
    cause: "Причина высокой/низкой оценки",
    reviews: "Отзыв о посещении магазина",
    shop: "Данные магазина"
  },
*/



export class ReviewsServices {
  /**
   * Request for get Reviews
   */
  static async getReviews(data: {
    offset?: number;
    limit?: number;
    search?: string;
    grade?: string
    // grade?: Array<number>
    dateTimeStart?: number
    dateTimeEnd?: number
    merchantId?: string
  }): Promise<ReviewsType[]> {
    const { offset = 0, limit = 30 } = data;

    const query: Array<string> = [];

    query.push(`offset=${offset}`);
    query.push(`limit=${limit}`);

    const response = await api.get(
      `${Endpoints.REVIEWS}${toQueryString(data)}`
    );

    return response.data.data;
  }

  // /**
  //  * Request for get Reviews by id
  //  */
  static async getReviewsById(id: number): Promise<ReviewsType> {
    const response = await api.get(`${Endpoints.REVIEWS}/${id}`);
    return response.data.data;
  }

  /**
   * export report
   */
  static async exportReport(data: {
    search?: string;
    grade?: string
    dateTimeStart?: number
    dateTimeEnd?: number
    merchantId?: string

  }): Promise<any> {
    const response = await api.get( `${Endpoints.REVIEWS_REPORT_EXPORT}${toQueryString(data)}`)
    return response.data.data
  }
}
