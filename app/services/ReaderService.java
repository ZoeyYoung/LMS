package services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Information;
import models.Point;
import models.Reader;
import models.ReaderCate;
import models.enums.PointType;

import org.springframework.stereotype.Service;

import repos.InformationRepository;
import repos.PointRepository;
import repos.ReaderRepository;
import utils.CommonUtils;
import utils.MD5Utils;
import views.table.SumPointTable;

@Named
@Singleton
@Service(value = "readerService")
public class ReaderService {
    @Inject
    private ReaderRepository readerRepository;
    @Inject
    private PointRepository pointRepository;
    @Inject
    InformationRepository informationRepository;

    public Reader newReader(Reader reader) {
        return readerRepository.save(reader);
    }

    public void updateReader(Reader reader) {
        readerRepository.save(reader);
    }

    public Iterable<Reader> allReaders() {
        return readerRepository.findAll();
    }

    public Reader findByReaderCode(String readerCode) {
        Iterator<Reader> it = readerRepository.findByReaderCode(readerCode).iterator();
        while (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public Reader findByReaderCodeAndPassword(String readerCode, String pwd) {
        Iterator<Reader> it = readerRepository.findByReaderCodeAndPassword(readerCode,
                MD5Utils.md5(pwd)).iterator();
        while (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public Iterable<Reader> findByReaderCate(ReaderCate readerCate) {
        return readerRepository.findByReaderCate(readerCate);
    }

    public Reader resetPassword(String readerCode) {
        Reader reader = findByReaderCode(readerCode);
        if (reader != null) {
            reader.password = MD5Utils.md5("000000");
            return readerRepository.save(reader);
        }
        return null;
    }

    /**
     *
     * @param reader
     * @return
     */
    public Reader savePassword(Reader reader) {
        return readerRepository.save(reader);
    }

    public Iterable<Information> findInformationForReader(String readerCode) {
        return informationRepository.findByCardID(readerCode);
    }

    public Information findInformationById(Long id) {
        return informationRepository.findOne(id);
    }

    public Iterable<Information> findInformations() {
        return informationRepository.findAllOrderByCreateDateDesc();
    }

    public Point newPoint(Reader reader, Long p, String source, String pt) {
        Point point = new Point();
        point.reader = reader;
        point.source = source;
        point.createDate = new Date();
        point.overdueDate = CommonUtils.afterDays(point.createDate, 365);
        point.ptype = PointType.valueOf(pt);
        point.point = p;
        return pointRepository.save(point);
    }

    public Point newPoint(Reader reader, Long p, String source, PointType pt) {
        Point point = new Point();
        point.reader = reader;
        point.source = source;
        point.createDate = new Date();
        point.overdueDate = CommonUtils.afterDays(point.createDate, 365);
        point.ptype = pt;
        point.point = p;
        return pointRepository.save(point);
    }

    public Iterable<Point> findPointsByReader(Reader reader) {
        return pointRepository.findByReaderOrderByCreateDateDesc(reader);
    }

    public Iterable<Point> findAllPoints() {
        return pointRepository.findOrderByCreateDateDesc();
    }

    public Point findPoint(Long id) {
        return pointRepository.findOne(id);
    }

    public void deletePoint(Long id) {
        pointRepository.delete(id);
    }

    public List<SumPointTable> findSumPoints() {
        Iterator<Object[]> iter = pointRepository.findSumPointGroupByReader(
                PointType.DONATION.ordinal(), PointType.EXCHANGE.ordinal()).iterator();
        List<SumPointTable> points = new ArrayList<SumPointTable>();
        while (iter.hasNext()) {
            Object[] obj = iter.next();
            SumPointTable point = new SumPointTable();
            point.reader = readerRepository.findOne(((BigInteger) obj[0]).longValue());
            if (obj[1] != null) {
                point.totalpoint = ((BigDecimal) obj[1]).longValue();
            }
            if (obj[2] != null) {
                point.point = ((BigDecimal) obj[2]).longValue();
            } else {
                point.point = 0L;
            }
            points.add(point);
        }
        return points;
    }

    public Point findSumPoint(Reader reader, Collection<PointType> list) {
        Iterator<Object[]> iter = pointRepository.findSumPointGroupByReader(reader, list)
                .iterator();
        while (iter.hasNext()) {
            Object[] obj = iter.next();
            Point point = new Point();
            point.reader = (Reader) obj[0];
            point.point = (Long) obj[1];
            return point;
        }
        return null;
    }

    public Iterable<Point> findExchangedSumPoint(Reader reader) {
        return pointRepository.findByReaderAndPtypeIn(reader, PointType.exchangedStatus());
    }

    public Point findSumPoint(Reader reader) {
        Iterator<Object[]> iter = pointRepository.findSumPointGroupByReader(reader).iterator();
        while (iter.hasNext()) {
            Object[] obj = iter.next();
            Point point = new Point();
            point.reader = (Reader) obj[0];
            point.point = (Long) obj[1];
            return point;
        }
        return null;
    }
}
